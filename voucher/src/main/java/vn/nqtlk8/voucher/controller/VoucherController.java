package vn.nqtlk8.voucher.controller;

import vn.nqtlk8.voucher.dto.response.VoucherResponse;
import vn.nqtlk8.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/promotions/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/available")
    public ResponseEntity<List<VoucherResponse>> getAvailableVouchers(
            @RequestParam("orderAmount") BigDecimal orderAmount) {

        List<VoucherResponse> vouchers = voucherService.getAvailableVouchersForOrder(orderAmount);
        return ResponseEntity.ok(vouchers);
    }
}
