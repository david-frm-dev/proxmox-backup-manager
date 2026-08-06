package com.daf.backend.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class LogWebSocketHandler extends TextWebSocketHandler {
    private final Map<UUID, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    private UUID extractJobId(WebSocketSession session) {
        if (session.getUri() == null) return null;

        String[] parts = session.getUri().getPath().split("/");
        return UUID.fromString(parts[3]);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID jobId = extractJobId(session);
        if (jobId == null) return;
        sessions.computeIfAbsent(jobId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID jobId = extractJobId(session);
        if (jobId == null) return;

        Set<WebSocketSession> sessionSet = sessions.get(jobId);
        if (sessionSet == null) return;

        sessionSet.remove(session);
    }

    public void send (UUID jobId, String line) {
        Set<WebSocketSession> sessionsForJob = sessions.getOrDefault(jobId, Set.of());

        for (WebSocketSession session : sessionsForJob) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(line));
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }
}
