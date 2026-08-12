package com.daf.backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RestoreQueueService {
    private static final String QUEUE_KEY = "restore:queue";
    private final StringRedisTemplate redisTemplate;

    public void enqueue(UUID jobId) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, jobId.toString());
        log.info("Restore job with Id: {} added to redis queue", jobId);
    }

    public String dequeue() {
        return redisTemplate.opsForList().rightPop(QUEUE_KEY, Duration.ofSeconds(5L));
    }
}
