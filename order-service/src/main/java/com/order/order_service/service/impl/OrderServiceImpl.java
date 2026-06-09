package com.order.order_service.service.impl;

import com.order.order_service.dto.event.*;
import com.order.order_service.dto.request.CreateOrderRequest;
import com.order.order_service.entity.Order;
import com.order.order_service.entity.OrderItem;
import com.order.order_service.entity.OrderStatus;
import com.order.order_service.kafka.producer.OrderProducer;
import com.order.order_service.repository.OrderItemRepository;
import com.order.order_service.repository.OrderRepository;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl
        implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderProducer orderProducer;

    @Override
    public String create_order(
            CreateOrderRequest request,
            String trace_id,
            String user_id) {

        String order_id =
                UUID.randomUUID().toString();

        Order order = Order.builder()
                .id(order_id)
                .user_id(user_id)
                .restaurant_id(request.getRestaurant_id())
                .voucher_code(request.getVoucher_code())
                .total_amount(request.getTotal_amount())
                .status(OrderStatus.PENDING_VALIDATION)
                .created_at(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        List<OrderItem> items =
                request.getItems()
                        .stream()
                        .map(item ->
                                OrderItem.builder()
                                        .order_id(order_id)
                                        .item_id(item.getItem_id())
                                        .name(item.getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                        .toList();

        orderItemRepository.saveAll(items);

        OrderCreatedEvent event =
                OrderCreatedEvent.builder()
                        .order_id(order_id)
                        .user_id(user_id)
                        .restaurant_id(request.getRestaurant_id())
                        .voucher_code(request.getVoucher_code())
                        .total_amount(request.getTotal_amount())
                        .customer_location(
                                CustomerLocationDto.builder()
                                        .lat(request.getLatitude())
                                        .lng(request.getLongitude())
                                        .build()
                        )
                        .items(
                                request.getItems()
                                        .stream()
                                        .map(item ->
                                                OrderItemEventDto.builder()
                                                        .item_id(item.getItem_id())
                                                        .name(item.getName())
                                                        .quantity(item.getQuantity())
                                                        .price(item.getPrice())
                                                        .build()
                                        )
                                        .toList()
                        )
                        .build();

        orderProducer.publish_order_created(
                event,
                trace_id,
                user_id);

        return order_id;
    }
    @Override
    public void applyVoucherAccepted(String orderId, BigDecimal finalAmount) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setFinal_amount(finalAmount);
        order.setStatus(OrderStatus.WAITING_FOR_DRIVER);

        orderRepository.save(order);
    }
    @Override
    public void applyVoucherRejected(
            String orderId,
            String errorCode) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PENDING_ADJUSTMENT);

        orderRepository.save(order);
    }
    @Override
    public void applyDriverAssigned(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.WAITING_FOR_DRIVER) {
            throw new RuntimeException(
                    "Order is not in WAITING_FOR_DRIVER state");
        }

        order.setStatus(OrderStatus.DRIVER_ASSIGNED);

        orderRepository.save(order);
    }
    @Override
    public void applyDeliveryCompleted(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DRIVER_ASSIGNED) {
            throw new RuntimeException(
                    "Order is not in DRIVER_ASSIGNED state");
        }

        order.setStatus(OrderStatus.COMPLETED);

        orderRepository.save(order);
    }
    @Override
    public void applyDriverNotFound(
            String orderId,
            String reasonCode,
            String traceId,
            String userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        OrderCancelledEvent event =
                OrderCancelledEvent.builder()
                        .order_id(order.getId())
                        .voucher_code(order.getVoucher_code())
                        .reason_code(reasonCode)
                        .build();

        orderProducer.publish_order_cancelled(
                event,
                traceId,
                userId
        );
    }
    @Override
    public void updateVoucher(
            String orderId,
            String voucherCode,
            String traceId,
            String userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_ADJUSTMENT) {
            throw new RuntimeException(
                    "Order is not in PENDING_ADJUSTMENT state");
        }

        order.setVoucher_code(voucherCode);

        orderRepository.save(order);

        OrderVoucherUpdatedEvent event =
                OrderVoucherUpdatedEvent.builder()
                        .order_id(orderId)
                        .new_voucher_code(voucherCode)
                        .total_amount(order.getTotal_amount())
                        .build();

        orderProducer.publish_order_voucher_updated(
                event,
                traceId,
                userId
        );
    }
    @Override
    public void removeVoucher(
            String orderId,
            String traceId,
            String userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_ADJUSTMENT) {
            throw new RuntimeException(
                    "Order is not in PENDING_ADJUSTMENT state");
        }

        order.setVoucher_code(null);

        orderRepository.save(order);

        VoucherAcceptedEvent event =
                VoucherAcceptedEvent.builder()
                        .order_id(orderId)
                        .final_amount(order.getTotal_amount())
                        .build();

        orderProducer.publish_voucher_accepted(
                event,
                traceId,
                userId
        );
    }
}
