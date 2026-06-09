package com.order.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent {

    private String order_id;

    private String voucher_code;

    private String reason_code;
}