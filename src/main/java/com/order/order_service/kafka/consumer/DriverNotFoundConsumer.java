package com.order.order_service.kafka.consumer;

import com.order.order_service.dto.event.DriverNotFoundEvent;
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
public class DriverNotFoundConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.DRIVER_NOT_FOUND
    )
    public void consume(
            DriverNotFoundEvent event,
            @Header(value = "X-Trace-Id", required = false) String trace_id,
            @Header(value = "X-User-Id", required = false) String user_id
    ) {
        try {

            log.info("==> [LOG 1] Đã nhận Driver Not Found Event!");

            if (trace_id == null)
                trace_id = UUID.randomUUID().toString();

            if (user_id == null)
                user_id = "SYSTEM_TEST";

            if (event == null || event.getOrder_id() == null) {
                log.warn("⏩ Event rỗng, bỏ qua!");
                return;
            }

            log.info(
                    "==> [LOG 2] Đang hủy đơn hàng {}",
                    event.getOrder_id()
            );

            orderService.applyDriverNotFound(
                    event.getOrder_id(),
                    event.getError_code(),
                    trace_id,
                    user_id
            );

            log.info(
                    "✅ [LOG 3] Đơn hàng {} đã chuyển CANCELLED",
                    event.getOrder_id()
            );

        } catch (Exception e) {

            log.error("❌ Chi tiết lỗi: {}", e.getMessage());
            log.error("❌ Stack Trace:", e);
        }
    }
}