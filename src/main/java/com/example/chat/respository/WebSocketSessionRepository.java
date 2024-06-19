package com.example.chat.respository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Repository
public class WebSocketSessionRepository {

    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<WebSocketSession>> subscriptions = new ConcurrentHashMap<>();

    public void save(WebSocketSession session) {
        sessions.putIfAbsent(session.getId(), session);
    }

    public WebSocketSession get(String sessionId) {
        return sessions.get(sessionId);
    }
}
