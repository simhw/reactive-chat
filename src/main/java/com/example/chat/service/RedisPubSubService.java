package com.example.chat.service;

import com.example.chat.domain.Chat;
import com.example.chat.domain.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPubSubService {
    private final ReactiveRedisTemplate<String, Object> template;
    private final ReactiveRedisMessageListenerContainer container;
    private final ObjectMapper objectMapper;

    private final Map<String, Sinks.Many<WebSocketMessage>> sinksMap = new ConcurrentHashMap<>();
    private final MessageSource messageSource;

    public Mono<Object> getChannels() {
        return template.opsForValue().get("*");
    }

    public Mono<Boolean> saveChannel(Chat chat) {
        return template.opsForValue().set(chat.getId(), chat);
    }

    public Mono<Long> publishMessage(Message message) {
        return template.convertAndSend(message.getChatId(), message);
    }

    public Flux<String> subscribeMessage(String chatId) {
        return container.receive(ChannelTopic.of(chatId))
                .map(ReactiveSubscription.Message::getMessage)
                .flatMap(message -> {
                    try {
                        return Flux.just(objectMapper.writeValueAsString(message));
                    } catch (Exception e) {
                        log.error("Error serializing message: {}", e.getMessage());
                        return Flux.error(new RuntimeException("Error serializing message", e));
                    }
                });
    }

    public Flux<Message> processMessage(WebSocketMessage socketMessage) {
        try {
            Message message = objectMapper.readValue(socketMessage.getPayloadAsText(), Message.class);
            return Flux.just(message);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}
