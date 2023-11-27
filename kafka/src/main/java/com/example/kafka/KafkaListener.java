package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class KafkaListener {

    private final KafkaListenerEndpointRegistry registry;
    private final SlidingWindowRateLimiter slidingWindowRateLimiter;

    public KafkaListener(KafkaListenerEndpointRegistry registry, SlidingWindowRateLimiter slidingWindowRateLimiter) {
        this.registry = registry;
        this.slidingWindowRateLimiter = slidingWindowRateLimiter;
    }


    @org.springframework.kafka.annotation.KafkaListener(groupId = "group", topics = "test")
    public void listen(ConsumerRecord<String, String> record) throws InterruptedException {
        log.info("Data Received V1 : {} {} {}", record.key(), record.value(), System.currentTimeMillis());
        int i = slidingWindowRateLimiter.allowRequest(CommonClass.key);
        if(i == 3){
            registry.getAllListenerContainers().parallelStream().forEach(MessageListenerContainer::pause);
            log.info("Pausing V1 HARD: {}", System.currentTimeMillis());
            Thread.sleep(1000);
            registry.getAllListenerContainers().parallelStream().forEach(MessageListenerContainer::resume);
            log.info("Resuming V1 HARD : {}", System.currentTimeMillis());
        }else if (i == 2){
            registry.getAllListenerContainers().parallelStream().forEach(MessageListenerContainer::pause);
            log.info("Pausing V1 SOFT: {}", System.currentTimeMillis());
            Thread.sleep(200);
            registry.getAllListenerContainers().parallelStream().forEach(MessageListenerContainer::resume);
            log.info("Resuming V1 SOFT : {}", System.currentTimeMillis());
        }
    }


}
