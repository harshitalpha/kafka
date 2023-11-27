package com.example.kafka;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class ReactiveKafkaConsumer {

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;
    private final DelayCalculator delayCalculator;
    private final SlidingWindowRateLimiter slidingWindowRateLimiter;

    public ReactiveKafkaConsumer(ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate, DelayCalculator delayCalculator, SlidingWindowRateLimiter slidingWindowRateLimiter) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.delayCalculator = delayCalculator;
        this.slidingWindowRateLimiter = slidingWindowRateLimiter;
    }

    @EventListener(ApplicationStartedEvent.class)
    public Flux<ReceiverRecord<String, String>> startKafkaConsumer() {
        Flux<ReceiverRecord<String, String>> flux = reactiveKafkaConsumerTemplate
                .receive()
                .doOnNext(receiverRecord -> log.info("Saving in the DB : {}, Offset : {}", receiverRecord.value(), receiverRecord.offset()))
                .doOnNext(record -> record.receiverOffset().acknowledge());

        flux.subscribe(new ReactiveKafkaConsumerSubscriber(slidingWindowRateLimiter));
        return flux;
    }
}
