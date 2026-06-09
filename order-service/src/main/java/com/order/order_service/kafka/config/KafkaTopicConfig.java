package com.order.order_service.kafka.config;

import com.order.order_service.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreated() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelled() {
        return TopicBuilder.name(KafkaTopics.ORDER_CANCELLED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderVoucherUpdated() {
        return TopicBuilder.name(KafkaTopics.ORDER_VOUCHER_UPDATED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic voucherAccepted() {
        return TopicBuilder.name(KafkaTopics.VOUCHER_ACCEPTED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic voucherRejected() {
        return TopicBuilder.name(KafkaTopics.VOUCHER_REJECTED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic driverAssigned() {
        return TopicBuilder.name(KafkaTopics.DRIVER_ASSIGNED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic driverNotFound() {
        return TopicBuilder.name(KafkaTopics.DRIVER_NOT_FOUND)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryCompleted() {
        return TopicBuilder.name(KafkaTopics.DELIVERY_COMPLETED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}