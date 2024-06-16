package com.example.chat.domain;

import lombok.Data;

@Data
public class Message {
    String sender;
    String content;
    String chatId;

    public Message() {
    }

    public Message(String sender, String content, String chatId) {
        this.sender = sender;
        this.content = content;
        this.chatId = chatId;
    }
}
