package vn.nqtlk8.voucher.event.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record VoucherAcceptedEvent(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("final_amount") BigDecimal finalAmount
) {}