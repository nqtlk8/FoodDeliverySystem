package vn.nqtlk8.voucher.event.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("restaurant_id") String restaurantId,
        @JsonProperty("voucher_code") String voucherCode,
        @JsonProperty("total_amount") BigDecimal totalAmount,
        @JsonProperty("customer_location") Location customerLocation,
        List<Item> items
) {
    public record Location(Double lat, Double lng) {}
    public record Item(@JsonProperty("item_id") String itemId, String name, Integer quantity, BigDecimal price) {}
}
