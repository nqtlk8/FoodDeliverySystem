package com.order.order_service.kafka.consumer;

import com.order.order_service.dto.event.VoucherRejectedEvent;
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
public class VoucherRejectedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.VOUCHER_REJECTED
    )
    public void consume(
            VoucherRejectedEvent event,
            @Header(value = "X-Trace-Id", required = false) String trace_id,
            @Header(value = "X-User-Id", required = false) String user_id
    ) {
        try {
            // [LOG 1]
            log.info("==> [LOG 1] Đã nhận tín hiệu Voucher Rejected từ Kafka!");

            if (trace_id == null) trace_id = UUID.randomUUID().toString();
            if (user_id == null) user_id = "SYSTEM_TEST";

            log.info("🎯 Nhận sự kiện Voucher Rejected: {}", event);
            log.info("TRACE = {}", trace_id);
            log.info("USER = {}", user_id);

            if (event == null || event.getOrder_id() == null) {
                log.warn("⏩ Dữ liệu Event bị trống hoặc rỗng, bỏ qua không xử lý!");
                return;
            }

            // [LOG 2]
            log.info(
                    "==> [LOG 2] Đang gọi orderService.applyVoucherRejected cho đơn hàng: {}",
                    event.getOrder_id()
            );

            orderService.applyVoucherRejected(
                    event.getOrder_id(),
                    event.getError_code()
            );

            // [LOG 3]
            log.info(
                    "✅ [LOG 3] Chuyển đơn hàng {} sang PENDING_ADJUSTMENT thành công!",
                    event.getOrder_id()
            );

        } catch (Exception e) {
            log.error("❌ [ERR LOG] Có lỗi xảy ra bên trong hàm xử lý logic!");
            log.error("❌ Chi tiết thông điệp lỗi: {}", e.getMessage());
            log.error("❌ Chi tiết dấu vết lỗi (Stack Trace): ", e);
        }
    }
}