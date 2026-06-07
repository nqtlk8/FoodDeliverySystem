package vn.nqtlk8.voucher.messaging;

import vn.nqtlk8.voucher.context.AppContextHolder;
import vn.nqtlk8.voucher.event.incoming.OrderCancelledEvent;
import vn.nqtlk8.voucher.event.incoming.OrderCreatedEvent;
import vn.nqtlk8.voucher.event.incoming.OrderVoucherUpdatedEvent;
import vn.nqtlk8.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherEventConsumer {

    private final VoucherService voucherService;

    @KafkaListener(topics = "order.created", groupId = "promotion-group")
    public void consumeOrderCreated(
            @Payload OrderCreatedEvent event,
            @Header(value = "X-Trace-Id", required = false) String traceId,
            @Header(value = "X-User-Id", required = false) String userId) {

        setupContext(traceId, userId);
        try {
            voucherService.processVoucherForOrder(event.orderId(), event.voucherCode(), event.totalAmount());
        } finally {
            AppContextHolder.clear();
        }
    }

    @KafkaListener(topics = "order.voucher.updated", groupId = "promotion-group")
    public void consumeOrderVoucherUpdated(
            @Payload OrderVoucherUpdatedEvent event,
            @Header(value = "X-Trace-Id", required = false) String traceId,
            @Header(value = "X-User-Id", required = false) String userId) {

        setupContext(traceId, userId);
        try {
            voucherService.processVoucherForOrder(event.orderId(), event.newVoucherCode(), event.totalAmount());
        } finally {
            AppContextHolder.clear();
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "promotion-group")
    public void consumeOrderCancelled(
            @Payload OrderCancelledEvent event,
            @Header(value = "X-Trace-Id", required = false) String traceId,
            @Header(value = "X-User-Id", required = false) String userId) {

        setupContext(traceId, userId);
        try {
            voucherService.rollbackVoucher(event.orderId());
        } finally {
            AppContextHolder.clear();
        }
    }

    private void setupContext(String traceId, String userId) {
        if (traceId != null) AppContextHolder.setTraceId(traceId);
        if (userId != null) AppContextHolder.setUserId(userId);
    }
}
