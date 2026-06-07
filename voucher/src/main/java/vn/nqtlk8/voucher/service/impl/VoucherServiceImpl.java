package vn.nqtlk8.voucher.service.impl;

import vn.nqtlk8.voucher.entity.Voucher;
import vn.nqtlk8.voucher.entity.VoucherUsage;
import vn.nqtlk8.voucher.event.outgoing.VoucherAcceptedEvent;
import vn.nqtlk8.voucher.event.outgoing.VoucherRejectedEvent;
import vn.nqtlk8.voucher.exception.PromoErrorCode;
import vn.nqtlk8.voucher.messaging.VoucherEventPublisher;
import vn.nqtlk8.voucher.repository.VoucherRepository;
import vn.nqtlk8.voucher.repository.VoucherUsageRepository;
import vn.nqtlk8.voucher.service.VoucherService;
import vn.nqtlk8.voucher.dto.response.VoucherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository usageRepository;
    private final VoucherEventPublisher eventPublisher;

    @Override
    public List<VoucherResponse> getAvailableVouchersForOrder(BigDecimal orderTotal) {
        return voucherRepository.findAvailableVouchers(orderTotal)
                .stream()
                .map(v -> new VoucherResponse(v.getCode(), v.getDiscountAmount(), v.getMinSpend()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processVoucherForOrder(String orderId, String voucherCode, BigDecimal totalAmount) {
        log.info("Processing voucher {} for order {}", voucherCode, orderId);

        if (voucherCode == null || voucherCode.isBlank()) {
            eventPublisher.publishAcceptedEvent(new VoucherAcceptedEvent(orderId, totalAmount));
            return;
        }

        Voucher voucher = voucherRepository.findById(voucherCode).orElse(null);

        if (voucher == null || "INACTIVE".equals(voucher.getStatus())) {
            eventPublisher.publishRejectedEvent(new VoucherRejectedEvent(orderId, PromoErrorCode.ERR_PROMO_EXPIRED.name()));
            return;
        }

        if (totalAmount.compareTo(voucher.getMinSpend()) < 0) {
            eventPublisher.publishRejectedEvent(new VoucherRejectedEvent(orderId, PromoErrorCode.ERR_PROMO_MIN_SPEND.name()));
            return;
        }

        int updatedRows = voucherRepository.deductVoucherUse(voucherCode);

        if (updatedRows > 0) {
            VoucherUsage usage = VoucherUsage.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(orderId)
                    .voucherCode(voucherCode)
                    .status("DEDUCTED")
                    .build();
            usageRepository.save(usage);

            BigDecimal finalAmount = totalAmount.subtract(voucher.getDiscountAmount());
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

            eventPublisher.publishAcceptedEvent(new VoucherAcceptedEvent(orderId, finalAmount));
        } else {
            eventPublisher.publishRejectedEvent(new VoucherRejectedEvent(orderId, PromoErrorCode.ERR_PROMO_FULL.name()));
        }
    }

    @Override
    @Transactional
    public void rollbackVoucher(String orderId) {
        usageRepository.findByOrderId(orderId).ifPresent(usage -> {
            if ("DEDUCTED".equals(usage.getStatus())) {
                voucherRepository.refundVoucherUse(usage.getVoucherCode());
                usage.setStatus("AVAILABLE");
                usageRepository.save(usage);
                log.info("Rollback successful for order {}", orderId);
            }
        });
    }
}
