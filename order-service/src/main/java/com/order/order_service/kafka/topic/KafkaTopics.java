package com.order.order_service.kafka.topic;

public class KafkaTopics {

    public static final String ORDER_CREATED =
            "order.created";

    public static final String VOUCHER_ACCEPTED =
            "voucher.accepted";

    public static final String VOUCHER_REJECTED =
            "voucher.rejected";

    public static final String ORDER_VOUCHER_UPDATED =
            "order.voucher.updated";

    public static final String DRIVER_ASSIGNED =
            "driver.assigned";

    public static final String DRIVER_NOT_FOUND =
            "driver.not-found";

    public static final String DELIVERY_COMPLETED =
            "delivery.completed";

    public static final String ORDER_CANCELLED =
            "order.cancelled";
}