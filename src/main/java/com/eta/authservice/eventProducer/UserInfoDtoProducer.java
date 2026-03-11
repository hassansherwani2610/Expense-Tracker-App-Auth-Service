package com.eta.authservice.eventProducer;

import com.eta.authservice.model.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoDtoProducer {
    public final KafkaTemplate<String, UserInfoDto> kafkaTemplate; // Kafka template used to publish messages to Kafka topics

    // Topic name injected from application.properties
    @Value("${spring.kafka.topic.name}")
    public String KAFKA_TOPIC;

    // Constructor injection for KafkaTemplate (recommended for testability)
    @Autowired
    public UserInfoDtoProducer(KafkaTemplate<String, UserInfoDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    // Sends user information event to Kafka
    public void sendEventToKafka(UserInfoDto userInfoDto){

        // Build Kafka message with payload and topic header
        Message<UserInfoDto> message = MessageBuilder
                .withPayload(userInfoDto)
                .setHeader(KafkaHeaders.TOPIC, KAFKA_TOPIC)
                .build();

        kafkaTemplate.send(message); // Publish event asynchronously to Kafka
    }
}