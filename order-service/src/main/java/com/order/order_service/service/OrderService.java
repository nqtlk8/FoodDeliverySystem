package com.order.order_service.service;

import com.order.order_service.dto.request.CreateOrderRequest;

import java.math.BigDecimal;

public interface OrderService {

    String create_order(
            CreateOrderRequest request);
    void applyVoucherAccepted(
            String orderId, BigDecimal finalAmount);

    void applyVoucherRejected(
            String orderId,
            String errorCode);
    void applyDriverAssigned(String orderId);
    void applyDeliveryCompleted(String orderId);
    void applyDriverNotFound(
            String orderId,
            String reasonCode);
    void updateVoucher(
            String orderId,
            String voucherCode);

    void removeVoucher(
            String orderId);
}