package vn.nqtlk8.voucher.service;

import vn.nqtlk8.voucher.dto.response.VoucherResponse;
import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAvailableVouchersForOrder(BigDecimal orderTotal);
    void processVoucherForOrder(String orderId, String voucherCode, BigDecimal totalAmount);
    void rollbackVoucher(String orderId);
}