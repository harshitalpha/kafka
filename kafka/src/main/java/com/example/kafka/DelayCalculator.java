package com.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DelayCalculator {

    private final SlidingWindowRateLimiter slidingWindowRateLimiter;

    public long getCurrentDelay (String key){
        int i = slidingWindowRateLimiter.allowRequest(key);
        if(i == 3){
            log.info("Delay : {}", 1000);
            return 1000L;
        }else if (i == 2){
            log.info("Delay : {}", 200);
            return 200L;
        }else {
            log.info("Delay : {}", 0);
            return 0;
        }
    }

}
