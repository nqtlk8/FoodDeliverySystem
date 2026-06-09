package com.order.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity

@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String order_id;

    private String item_id;

    private String name;

    private Integer quantity;

    private BigDecimal price;
}
