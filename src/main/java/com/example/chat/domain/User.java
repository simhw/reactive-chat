package com.example.chat.domain;

import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketSession;

@Data
public class User {
    private String id;
    private String name;
    private int age;
    private WebSocketSession session;
}
