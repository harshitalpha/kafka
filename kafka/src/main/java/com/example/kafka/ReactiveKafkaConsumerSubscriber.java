package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;

@Slf4j
public class ReactiveKafkaConsumerSubscriber extends BaseSubscriber<ConsumerRecord<String, String>> {

    private final SlidingWindowRateLimiter slidingWindowRateLimiter;


    public ReactiveKafkaConsumerSubscriber(SlidingWindowRateLimiter slidingWindowRateLimiter) {
        this.slidingWindowRateLimiter = slidingWindowRateLimiter;
    }


    @Override
    public void hookOnSubscribe(Subscription subscription) {
        request(1);
    }

    @Override
    public void hookOnNext(ConsumerRecord<String, String> request) {
        int rateLimitResult = slidingWindowRateLimiter.allowRequest(CommonClass.key);
        if (rateLimitResult == 1) {
            log.info("[INFO][{}][hookOnNext] Processing Message Allowed : {}", this.getClass().getSimpleName(), request.value());
            request(1);
        } else if (rateLimitResult == 2) {
            long delayMillis = (1000 / 200);
            log.info("[INFO][{}][hookOnNext] Processing Message Softly : {}", this.getClass().getSimpleName(), request.value());
            Mono.delay(Duration.ofMillis(delayMillis))
                    .subscribe(t -> request(1));
        } else if(rateLimitResult == 3){
            long delayMillis = 1000;
            log.info("[INFO][{}][hookOnNext] Delaying Hard Limit reached: {}", this.getClass().getSimpleName(), delayMillis);
            Mono.delay(Duration.ofMillis(delayMillis))
                    .subscribe(t -> this.onNext(request));
        }
    }

}
