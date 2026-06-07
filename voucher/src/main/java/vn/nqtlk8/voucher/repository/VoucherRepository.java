package vn.nqtlk8.voucher.repository;

import vn.nqtlk8.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' " +
            "AND v.expiryDate > CURRENT_TIMESTAMP " +
            "AND v.currentUses < v.maxUses " +
            "AND v.minSpend <= :orderTotal")
    List<Voucher> findAvailableVouchers(@Param("orderTotal") BigDecimal orderTotal);

    @Modifying
    @Query("UPDATE Voucher v SET v.currentUses = v.currentUses + 1 " +
            "WHERE v.code = :code AND v.status = 'ACTIVE' " +
            "AND v.expiryDate > CURRENT_TIMESTAMP AND v.currentUses < v.maxUses")
    int deductVoucherUse(@Param("code") String code);

    @Modifying
    @Query("UPDATE Voucher v SET v.currentUses = v.currentUses - 1 " +
            "WHERE v.code = :code AND v.currentUses > 0")
    int refundVoucherUse(@Param("code") String code);
}
