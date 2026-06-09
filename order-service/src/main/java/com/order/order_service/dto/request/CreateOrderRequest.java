package com.order.order_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    private String restaurant_id;

    private String voucher_code;

    private BigDecimal total_amount;

    private Double latitude;

    private Double longitude;

    private List<OrderItemRequest> items;
}