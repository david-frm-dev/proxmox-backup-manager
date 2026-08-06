package com.daf.backend.websocket;

import com.daf.backend.service.JobLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class LogRedisSubscriber implements MessageListener {
    private final LogWebSocketHandler logHandler;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String fmtChannel = new String (message.getChannel());
        String fmtMessage = new String (message.getBody());
        UUID jobId = UUID.fromString(fmtChannel.substring(JobLogService.CHANNEL_PREFIX.length()));
        logHandler.send(jobId, fmtMessage);
    }
}
