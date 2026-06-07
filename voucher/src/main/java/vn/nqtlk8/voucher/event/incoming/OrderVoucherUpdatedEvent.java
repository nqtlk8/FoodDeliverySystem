package vn.nqtlk8.voucher.event.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record OrderVoucherUpdatedEvent(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("new_voucher_code") String newVoucherCode,
        @JsonProperty("total_amount") BigDecimal totalAmount
) {}