package vn.nqtlk8.voucher.event.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderCancelledEvent(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("voucher_code") String voucherCode,
        @JsonProperty("reason_code") String reasonCode
) {}
