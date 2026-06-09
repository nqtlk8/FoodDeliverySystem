package com.order.order_service.kafka.consumer;

import com.order.order_service.dto.event.DriverAssignedEvent;
import com.order.order_service.kafka.topic.KafkaTopics;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverAssignedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.DRIVER_ASSIGNED
    )
    public void consume(
            DriverAssignedEvent event,
            @Header(value = "X-Trace-Id", required = false) String trace_id,
            @Header(value = "X-User-Id", required = false) String user_id
    ) {
        try {

            log.info("==> [LOG 1] Đã nhận Driver Assigned Event!");

            if (trace_id == null)
                trace_id = UUID.randomUUID().toString();

            if (user_id == null)
                user_id = "SYSTEM_TEST";

            log.info("🎯 EVENT = {}", event);
            log.info("TRACE = {}", trace_id);
            log.info("USER = {}", user_id);

            if (event == null || event.getOrder_id() == null) {
                log.warn("⏩ Event rỗng, bỏ qua!");
                return;
            }

            log.info(
                    "==> [LOG 2] Đang cập nhật trạng thái DRIVER_ASSIGNED cho đơn {}",
                    event.getOrder_id()
            );

            orderService.applyDriverAssigned(
                    event.getOrder_id()
            );

            log.info(
                    "✅ [LOG 3] Đơn hàng {} đã chuyển sang DRIVER_ASSIGNED",
                    event.getOrder_id()
            );

        } catch (Exception e) {

            log.error("❌ Có lỗi xảy ra!");
            log.error("❌ Message: {}", e.getMessage());
            log.error("❌ Stack Trace:", e);
        }
    }
}