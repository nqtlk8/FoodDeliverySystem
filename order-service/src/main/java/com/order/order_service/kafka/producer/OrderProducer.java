package com.order.order_service.kafka.producer;

import com.order.order_service.dto.event.OrderCancelledEvent;
import com.order.order_service.dto.event.OrderCreatedEvent;
import com.order.order_service.dto.event.OrderVoucherUpdatedEvent;
import com.order.order_service.dto.event.VoucherAcceptedEvent;

public interface OrderProducer {

    void publish_order_created(
            OrderCreatedEvent event);
    void publish_order_cancelled(
            OrderCancelledEvent event);
    void publish_order_voucher_updated(
            OrderVoucherUpdatedEvent event);

    void publish_voucher_accepted(
            VoucherAcceptedEvent event);
}
