package com.order.order_service.controller;

import com.order.order_service.dto.request.CreateOrderRequest;
import com.order.order_service.dto.request.UpdateVoucherRequest;
import com.order.order_service.entity.Order;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> create_order(

            @RequestHeader("X-Trace-Id")
            String trace_id,

            @RequestHeader("X-User-Id")
            String user_id,

            @RequestBody
            CreateOrderRequest request) {

        return ResponseEntity.ok(
                orderService.create_order(
                        request,
                        trace_id,
                        user_id));
    }
    @PutMapping("/{orderId}/voucher")
    public ResponseEntity<Void> updateVoucher(

            @PathVariable String orderId,

            @RequestHeader("X-Trace-Id")
            String traceId,

            @RequestHeader("X-User-Id")
            String userId,

            @RequestBody
            UpdateVoucherRequest request) {

        orderService.updateVoucher(
                orderId,
                request.getNew_voucher_code(),
                traceId,
                userId);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/{orderId}/remove-voucher")
    public ResponseEntity<Void> removeVoucher(

            @PathVariable String orderId,

            @RequestHeader("X-Trace-Id")
            String traceId,

            @RequestHeader("X-User-Id")
            String userId) {

        orderService.removeVoucher(
                orderId,
                traceId,
                userId);

        return ResponseEntity.ok().build();
    }
}