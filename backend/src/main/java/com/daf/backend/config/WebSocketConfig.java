package com.daf.backend.config;

import com.daf.backend.service.JobLogService;
import com.daf.backend.websocket.LogRedisSubscriber;
import com.daf.backend.websocket.LogWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final LogWebSocketHandler logHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //TODO POLICY
        registry.addHandler(logHandler, "/ws/jobs/*/logs").setAllowedOrigins("*");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory, LogRedisSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, new PatternTopic(JobLogService.CHANNEL_PREFIX + "*"));
        return container;
    }
}
