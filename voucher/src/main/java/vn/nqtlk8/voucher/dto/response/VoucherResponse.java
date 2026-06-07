package vn.nqtlk8.voucher.dto.response;

import java.math.BigDecimal;

public record VoucherResponse(
        String code,
        BigDecimal discountAmount,
        BigDecimal minSpend
) {}
