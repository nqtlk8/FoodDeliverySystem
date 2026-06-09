package com.order.order_service.kafka.consumer;

import com.order.order_service.dto.event.VoucherAcceptedEvent;
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
public class VoucherAcceptedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.VOUCHER_ACCEPTED
    )
    public void consume(
            VoucherAcceptedEvent event,
            @Header(value = "X-Trace-Id", required = false) String trace_id,
            @Header(value = "X-User-Id", required = false) String user_id
    ) {
        try {
            // [LOG 1] Xác nhận đã nhảy vào hàm consume thành công
            log.info("==> [LOG 1] Đã nhận tín hiệu sự kiện từ Kafka Listener!");

            if (trace_id == null) trace_id = UUID.randomUUID().toString();
            if (user_id == null) user_id = "SYSTEM_TEST";

            log.info("🎯 Nhận sự kiện Voucher Accepted: {}", event);
            log.info("TRACE = {}", trace_id);
            log.info("USER = {}", user_id);

            if (event == null || event.getOrder_id() == null) {
                log.warn("⏩ Dữ liệu Event bị trống hoặc rỗng, bỏ qua không xử lý!");
                return;
            }

            // [LOG 2] Báo trước khi gọi dịch vụ lưu DB
            log.info("==> [LOG 2] Đang gọi orderService.applyVoucherAccepted cho đơn hàng: {}", event.getOrder_id());

            orderService.applyVoucherAccepted(
                    event.getOrder_id(),
                    event.getFinal_amount()
            );

            // [LOG 3] Hoàn thành công việc
            log.info("✅ [LOG 3] Xử lý áp dụng Voucher thành công cho đơn hàng: {}", event.getOrder_id());

        } catch (Exception e) {
            // [ERR LOG] In chi tiết toàn bộ nguyên nhân và dấu vết lỗi cụ thể tại đây
            log.error("❌ [ERR LOG] Có lỗi xảy ra bên trong hàm xử lý logic!");
            log.error("❌ Chi tiết thông điệp lỗi: {}", e.getMessage());
            log.error("❌ Chi tiết dấu vết lỗi (Stack Trace): ", e);
        }
    }
}