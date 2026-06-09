package com.order.order_service.service;

import com.order.order_service.dto.event.OrderCancelledEvent;
import com.order.order_service.entity.Order;
import com.order.order_service.entity.OrderStatus;
import com.order.order_service.kafka.producer.OrderProducer;
import com.order.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTask {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

//    @Scheduled(fixedRate = 10000)
//    public void autoExpireOrders() {
//
//        LocalDateTime threshold =
//                LocalDateTime.now().minusSeconds(60); //test nhanh với 60s
//
//        List<Order> expiredOrders =
//                orderRepository.findAll()
//                        .stream()
//                        .filter(o ->
//                                o.getStatus() == OrderStatus.PENDING_VALIDATION
//                                        || o.getStatus() == OrderStatus.PENDING_ADJUSTMENT)
//                        .filter(o ->
//                                o.getCreated_at() != null
//                                        && o.getCreated_at().isBefore(threshold))
//                        .toList();
//
//        for (Order order : expiredOrders) {
//
//            order.setStatus(OrderStatus.EXPIRED);
//
//            orderRepository.save(order);
//
//            OrderCancelledEvent event =
//                    OrderCancelledEvent.builder()
//                            .order_id(order.getId())
//                            .voucher_code(order.getVoucher_code())
//                            .reason_code("ERR_ORDER_TIMEOUT")
//                            .build();
//
//            orderProducer.publish_order_cancelled(
//                    event,
//                    "SYSTEM",
//                    "SYSTEM"
//            );
//
//            log.info(
//                    "⏰ Order {} đã EXPIRED và phát Order_Cancelled",
//                    order.getId()
//            );
//        }
//   }
}