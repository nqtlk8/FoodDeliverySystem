package com.order.order_service.controller;

import com.order.order_service.dto.request.CreateOrderRequest;
import com.order.order_service.dto.request.UpdateVoucherRequest;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders") // Đã đổi thành /v1/orders
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Tạo đơn hàng mới
    @PostMapping
    public ResponseEntity<String> create_order(
            @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(
                orderService.create_order(request)
        );
    }

    // 2. Cập nhật/Áp dụng Voucher mới cho đơn hàng
    @PutMapping("/{orderId}/voucher")
    public ResponseEntity<Void> updateVoucher(
            @PathVariable String orderId,
            @RequestBody UpdateVoucherRequest request) {

        orderService.updateVoucher(
                orderId,
                request.getNew_voucher_code()
        );
        return ResponseEntity.ok().build();
    }

    // 3. Xóa/Gỡ Voucher khỏi đơn hàng (Đổi từ POST sang DELETE)
    @DeleteMapping("/{orderId}/voucher")
    public ResponseEntity<Void> removeVoucher(
            @PathVariable String orderId) {

        orderService.removeVoucher(
                orderId
        );
        return ResponseEntity.ok().build();
    }
}