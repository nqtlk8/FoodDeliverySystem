package vn.nqtlk8.voucher.event.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoucherRejectedEvent(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("error_code") String errorCode
) {}
