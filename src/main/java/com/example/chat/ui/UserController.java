package com.example.chat.ui;

import com.example.chat.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ReactiveRedisOperations<String, Object> operations;

    @PostMapping("")
    public Mono<Long> save(@RequestBody User user) {
        return this.operations.convertAndSend("users", user);
    }
}
