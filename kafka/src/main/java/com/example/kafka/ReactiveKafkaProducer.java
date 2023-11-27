package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ReactiveKafkaProducer {

    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;

    public ReactiveKafkaProducer(ReactiveKafkaProducerTemplate reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void publishMessage (String message){
        reactiveKafkaProducerTemplate.send("reactive_test", message)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", message, senderResult.recordMetadata().offset()))
                .subscribe();
    }

    public void publishBulkMessage (int count){
        for (int i = 0; i < count; i++) {
            String message = "DATA "+i;
            reactiveKafkaProducerTemplate.send("reactive_test", message)
                    .doOnSuccess(senderResult -> log.info("sent {} offset : {}", message, senderResult.recordMetadata().offset()))
                    .subscribe();
        }
    }




}
