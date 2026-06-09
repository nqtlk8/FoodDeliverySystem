package com.order.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCompletedEvent {

    private String order_id;

    private String driver_id;

    private String delivered_at;
}