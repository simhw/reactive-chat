package com.example.chat;

import com.example.chat.respository.WebSocketSessionRepository;
import com.example.chat.service.RedisPubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final Duration PING_INTERVAL = Duration.ofSeconds(25);
    private static final byte[] PING_PAYLOAD = new byte[0];

    private final RedisPubSubService pubSubService;
    private final WebSocketSessionRepository sessionRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessionRepository.save(session);

        WebSocketMessage sessionIdMessage = session.textMessage("{\"sessionId\":\"" + session.getId() + "\"}");
        Mono<Void> sessionId = session.send(Mono.just(sessionIdMessage));

        Flux<WebSocketMessage> pingSource = Flux.interval(PING_INTERVAL)
                .map(i -> session.pingMessage(factory -> factory.wrap(PING_PAYLOAD)));

        Mono<Void> input = session.receive()
                .filter(message -> message.getType() == WebSocketMessage.Type.TEXT)
                .concatMap(pubSubService::publishMessage)
                .then();

        Flux<WebSocketMessage> output = Flux.defer(() -> {
            Set<String> chatIds = sessionRepository.getChatIds(session);

            return Flux.fromIterable(chatIds)
                    .flatMap(pubSubService::subscribeMessage)
                    .map(message -> {
                        try {
                            return session.textMessage(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        });

        return sessionId.then(session.send(output)).and(input)
                .doFinally(signalType -> {
                    sessionRepository.remove(session.getId());
                });

        /**
         Flux<WebSocketMessage> messageSource = session.receive()
            .map(value -> session.textMessage("Echo " + value.getPayloadAsText()));
         Mono<Void> output = session.send(Flux.merge(source, pingSource));

         return Mono.zip(output, input).then();
         */
    }

    /**
    private void broadcast(String message) {
        sessions.values()
                .forEach(session -> session.send(Mono.just(session.textMessage(message))).subscribe());
    }
     */
}
