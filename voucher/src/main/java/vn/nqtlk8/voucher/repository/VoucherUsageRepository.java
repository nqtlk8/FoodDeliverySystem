package vn.nqtlk8.voucher.repository;

import vn.nqtlk8.voucher.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, String> {
    Optional<VoucherUsage> findByOrderId(String orderId);
}