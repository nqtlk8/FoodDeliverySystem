package com.order.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private String id;

    private String user_id;

    private String restaurant_id;

    private BigDecimal total_amount;

    private BigDecimal final_amount;

    private String voucher_code;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime created_at;
}