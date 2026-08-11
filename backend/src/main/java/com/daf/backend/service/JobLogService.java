package com.daf.backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class JobLogService {
    private final StringRedisTemplate redisTemplate;
    public static final String CHANNEL_PREFIX = "logs:";

    public void publish(UUID jobId, String line) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + jobId, line);
    }
}
