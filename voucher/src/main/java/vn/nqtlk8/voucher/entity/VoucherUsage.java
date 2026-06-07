package vn.nqtlk8.voucher.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "voucher_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsage {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, length = 36, unique = true)
    private String orderId;

    @Column(name = "voucher_code", nullable = false, length = 50)
    private String voucherCode;

    @Column(nullable = false, length = 20)
    private String status;
}
