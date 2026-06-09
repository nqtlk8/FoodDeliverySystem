package com.order.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVoucherUpdatedEvent {

    private String order_id;

    private String new_voucher_code;

    private BigDecimal total_amount;
}