package com.example.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMessage (String message, String partition){
        kafkaTemplate.send("new_reactive_test", String.valueOf(UUID.randomUUID()), message);
    }

    public void publishMessageBulk (int count){
        for (int i = 0; i < count; i++){
            String msg = "DATA : " + i;
            kafkaTemplate.send("new_reactive_test", String.valueOf(i), msg);
        }
    }

}
