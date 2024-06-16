package com.example.chat.ui;

import com.example.chat.domain.Chat;
import com.example.chat.domain.Message;
import com.example.chat.respository.WebSocketSessionRepository;
import com.example.chat.service.RedisPubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(value = "/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final RedisPubSubService pubSubService;
    private final WebSocketSessionRepository sessionRepository;

    @PostMapping("")
    public void createChat(@RequestBody Chat chat) {
        chat.setId(UUID.randomUUID().toString());
        pubSubService.subscribe(chat.getId());
    }

    @GetMapping
    public void subscribeChat(@RequestParam String chatId, @RequestParam String sessionId) {
        pubSubService.subscribe(sessionId, chatId);
    }
}
