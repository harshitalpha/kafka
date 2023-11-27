package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SlidingWindowRateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    public SlidingWindowRateLimiter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int allowRequest(String requestKey) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - 1000;

        Long HARD_LIMIT = 10L;
        Long SOFT_LIMIT = 5L;
        Long currentCount = redisTemplate.opsForZSet().count(requestKey, windowStart, currentTime);

        if (currentCount == null) {
            currentCount = 0L;
        }
        log.info("[INFO][{}][allowRequest] Current Call count : {} ",this.getClass().getSimpleName(), currentCount);

        if(currentCount >= HARD_LIMIT){
            return 3;
        }else if(currentCount >= SOFT_LIMIT){
            redisTemplate.opsForZSet().add(requestKey, currentTime, currentTime);
            return 2;
        }else {
            redisTemplate.opsForZSet().add(requestKey, currentTime, currentTime);
            return 1;
        }

    }



}
