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
public class BackupQueueService {
    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "backup:queue";

    public void enqueue(UUID jobId) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, jobId.toString());
        log.info("Backup-Job with Id: {} added to redis queue", jobId);
    }

    public String dequeue() {
        return redisTemplate.opsForList().rightPop(QUEUE_KEY, Duration.ofSeconds(5L));
    }
}
