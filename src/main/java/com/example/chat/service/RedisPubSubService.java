package com.example.chat.service;

import com.example.chat.domain.Message;
import com.example.chat.respository.WebSocketSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPubSubService {

    private final ReactiveRedisTemplate<String, Object> template;
    private final ReactiveRedisMessageListenerContainer container;
    private final ObjectMapper objectMapper;
    private final WebSocketSessionRepository sessionRepository;

    public Mono<Long> publishMessage(WebSocketMessage socketMessage) {
        try {
            Message message = objectMapper.readValue(socketMessage.getPayloadAsText(), Message.class);
            return template.convertAndSend(message.getChatId(), message);

        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.empty();
        }
    }

    public Flux<String> subscribeMessage(String chatId) {
        return container.receive(ChannelTopic.of(chatId))
                .map(ReactiveSubscription.Message::getMessage);
    }

    public void subscribe(String chatId) {
        container.receive(ChannelTopic.of(chatId))
                .subscribe();
    }

    public void subscribe(String sessionId, String chatId) {
        sessionRepository.add(sessionId, chatId);
    }
}
