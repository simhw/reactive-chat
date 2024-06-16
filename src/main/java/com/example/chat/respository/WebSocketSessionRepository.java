package com.example.chat.respository;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class WebSocketSessionRepository {

    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<WebSocketSession, Set<String>> subscriptions = new ConcurrentHashMap<>();

    public Set<String> getChatIds(WebSocketSession session) {
        return subscriptions.get(session);
    }

    public void save(WebSocketSession session) {
        sessions.put(session.getId(), session);
        subscriptions.put(session, new HashSet<>());
    }

    public void add(String sessionId, String chatId) {
        WebSocketSession webSocketSession = sessions.get(sessionId);
        subscriptions.get(webSocketSession).add(chatId);
    }

    public void remove(String sessionId) {
        WebSocketSession webSocketSession = sessions.remove(sessionId);
        subscriptions.remove(webSocketSession);
    }
}
