package com.order.order_service.kafka.consumer;

import com.order.order_service.dto.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @KafkaListener(
            topics = "order.created")
            //groupId = "test-group")
    public void consume(

            OrderCreatedEvent event,

            @Header("X-Trace-Id")
            String trace_id,

            @Header("X-User-Id")
            String user_id) {

        log.info("EVENT = {}", event);
        log.info("TRACE = {}", trace_id);
        log.info("USER = {}", user_id);
    }
}
