package com.example.chat.domain;

import lombok.Data;

@Data
public class Chat {
    private String id;
    private String name;

    public Chat() {
    }

    public Chat(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
