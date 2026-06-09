package com.order.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String order_id;

    private String user_id;

    private String restaurant_id;

    private String voucher_code;

    private BigDecimal total_amount;

    private CustomerLocationDto customer_location;

    private List<OrderItemEventDto> items;
}
