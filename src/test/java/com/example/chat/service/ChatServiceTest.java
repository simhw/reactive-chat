package com.example.chat.service;

import com.example.chat.domain.Chat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    ReactiveStringRedisTemplate stringRedisTemplate;

    @Autowired
    ReactiveRedisTemplate<String, Object> redisTemplate;

    @Test
    void setStringRedisTemplate() {
        // given
        Chat chat = new Chat();
        String chatId = UUID.randomUUID().toString();
        chat.setId(chatId);
        chat.setName("chat");

        // when
        Mono<Boolean> operation = stringRedisTemplate.opsForValue()
                .set(chatId, chat.toString())
                .flatMap(result -> {
                    if (result) {
                        return stringRedisTemplate.opsForValue()
                                .get(chatId)
                                .doOnNext(System.out::println)
                                .map(value -> chatId.equals(value.toString()));
                    } else {
                        return Mono.just(false);
                    }
                });

        // then
        Assertions.assertThat(operation.block()).isTrue();
    }

    @Test
    void setRedisTemplate() {
        // given
        Chat chat = new Chat();
        String chatId = UUID.randomUUID().toString();
        chat.setId(chatId);
        chat.setName("test2");

        // when
        Mono<Boolean> result = redisTemplate.opsForValue()
                .set(chatId, chat);

        // then
        Assertions.assertThat(result.block()).isTrue();
    }
}