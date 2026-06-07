package vn.nqtlk8.voucher.messaging;

import vn.nqtlk8.voucher.context.AppContextHolder;
import vn.nqtlk8.voucher.event.outgoing.VoucherAcceptedEvent;
import vn.nqtlk8.voucher.event.outgoing.VoucherRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class VoucherEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAcceptedEvent(VoucherAcceptedEvent event) {
        sendWithHeaders("voucher.accepted", event.orderId(), event);
    }

    public void publishRejectedEvent(VoucherRejectedEvent event) {
        sendWithHeaders("voucher.rejected", event.orderId(), event);
    }

    // Hàm tiện ích để nhúng Header từ Thread hiện tại vào Kafka Record
    private void sendWithHeaders(String topic, String key, Object payload) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, payload);

        String traceId = AppContextHolder.getTraceId();
        String userId = AppContextHolder.getUserId();

        if (traceId != null) {
            record.headers().add("X-Trace-Id", traceId.getBytes(StandardCharsets.UTF_8));
        }
        if (userId != null) {
            record.headers().add("X-User-Id", userId.getBytes(StandardCharsets.UTF_8));
        }

        kafkaTemplate.send(record);
    }
}
