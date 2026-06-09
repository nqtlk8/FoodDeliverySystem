package com.order.order_service.dto.event;

import com.order.order_service.dto.event.OrderItemEventDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignedEvent {
    private String order_id;
    private String restaurant_id;
    private String driver_id;
    private String driver_name;
    private String driver_plate;
    private Integer estimated_duration;
    private List<OrderItemEventDto> items;
}