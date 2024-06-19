package com.example.chat.ui;

import com.example.chat.domain.Chat;
import com.example.chat.domain.Message;
import com.example.chat.respository.WebSocketSessionRepository;
import com.example.chat.service.RedisPubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(value = "/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final RedisPubSubService pubSubService;
    private final WebSocketSessionRepository sessionRepository;

    @GetMapping("")
    public Mono<Object> getChats() {
        return pubSubService.getChannels();
    }

    @PostMapping("")
    public Mono<Boolean> createChat(@RequestBody Chat chat) {
        chat.setId(UUID.randomUUID().toString());
        Mono<Boolean> result = pubSubService.saveChannel(chat);
        return Mono.just(result).hasElement();
    }

    @PostMapping("/message")
    public Mono<Long> sendMessage(@RequestBody Message message) {
        Mono<Long> result = pubSubService.publishMessage(message);
        return result;
    }
}
