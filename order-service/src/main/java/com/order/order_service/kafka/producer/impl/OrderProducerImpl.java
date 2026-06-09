package com.order.order_service.kafka.producer.impl;

import com.order.order_service.dto.event.OrderCancelledEvent;
import com.order.order_service.dto.event.OrderCreatedEvent;
import com.order.order_service.dto.event.OrderVoucherUpdatedEvent;
import com.order.order_service.dto.event.VoucherAcceptedEvent;
import com.order.order_service.kafka.producer.OrderProducer;
import com.order.order_service.kafka.topic.KafkaTopics;
import com.order.order_service.context.AppContextHolder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OrderProducerImpl
        implements OrderProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public void publish_order_created(
            OrderCreatedEvent event) {

        ProducerRecord<String,Object> record =
                new ProducerRecord<>(
                        KafkaTopics.ORDER_CREATED,
                        event.getOrder_id(),
                        event);

        String trace_id = AppContextHolder.getTraceId() != null ? AppContextHolder.getTraceId() : UUID.randomUUID().toString();
        String user_id = AppContextHolder.getUserId() != null ? AppContextHolder.getUserId() : "SYSTEM";

        record.headers().add(
                "X-Trace-Id",
                trace_id.getBytes(StandardCharsets.UTF_8));

        record.headers().add(
                "X-User-Id",
                user_id.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
    @Override
    public void publish_order_cancelled(
            OrderCancelledEvent event) {

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(
                        KafkaTopics.ORDER_CANCELLED,
                        event.getOrder_id(),
                        event
                );

        String trace_id = AppContextHolder.getTraceId() != null ? AppContextHolder.getTraceId() : UUID.randomUUID().toString();
        String user_id = AppContextHolder.getUserId() != null ? AppContextHolder.getUserId() : "SYSTEM";

        record.headers().add(
                "X-Trace-Id",
                trace_id.getBytes(StandardCharsets.UTF_8));

        record.headers().add(
                "X-User-Id",
                user_id.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
    @Override
    public void publish_order_voucher_updated(
            OrderVoucherUpdatedEvent event) {

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(
                        KafkaTopics.ORDER_VOUCHER_UPDATED,
                        event.getOrder_id(),
                        event
                );

        String trace_id = AppContextHolder.getTraceId() != null ? AppContextHolder.getTraceId() : UUID.randomUUID().toString();
        String user_id = AppContextHolder.getUserId() != null ? AppContextHolder.getUserId() : "SYSTEM";

        record.headers().add(
                "X-Trace-Id",
                trace_id.getBytes(StandardCharsets.UTF_8));

        record.headers().add(
                "X-User-Id",
                user_id.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
    @Override
    public void publish_voucher_accepted(
            VoucherAcceptedEvent event) {

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(
                        KafkaTopics.VOUCHER_ACCEPTED,
                        event.getOrder_id(),
                        event
                );

        String trace_id = AppContextHolder.getTraceId() != null ? AppContextHolder.getTraceId() : UUID.randomUUID().toString();
        String user_id = AppContextHolder.getUserId() != null ? AppContextHolder.getUserId() : "SYSTEM";

        record.headers().add(
                "X-Trace-Id",
                trace_id.getBytes(StandardCharsets.UTF_8));

        record.headers().add(
                "X-User-Id",
                user_id.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
}
