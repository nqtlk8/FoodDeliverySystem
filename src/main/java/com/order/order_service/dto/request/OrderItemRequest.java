package com.order.order_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    private String item_id;

    private String name;

    private Integer quantity;

    private BigDecimal price;
}
