package com.example.chat;

import com.example.chat.domain.Message;
import com.example.chat.service.RedisPubSubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

import static org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final Duration PING_INTERVAL = Duration.ofSeconds(25);
    private static final byte[] PING_PAYLOAD = new byte[0];

    private final RedisPubSubService pubSubService;

    @Override
    public Mono<Void> handle(WebSocketSession socketSession) {
        Flux<WebSocketMessage> pingSource = Flux.interval(PING_INTERVAL)
                .map(i -> socketSession.pingMessage(factory -> factory.wrap(PING_PAYLOAD)));

        // send messages to client
        Flux<WebSocketMessage> output = socketSession.receive()
                .filter(socketMessage -> socketMessage.getType() == TEXT)
                .flatMap(pubSubService::processMessage)
                .flatMap(message -> pubSubService.subscribeMessage(message.getChatId()))
                .map(socketSession::textMessage)
                .doOnError(e -> log.error("Error processing output message", e));

        return socketSession.send(Flux.merge(output, pingSource));
    }
}
