package com.example.chat.respository;

import com.example.chat.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final ReactiveRedisOperations<String, Object> operations;

    public Mono<String> saveUser(User user) {
        user.setId(UUID.randomUUID().toString());
        Duration ttl = Duration.ofSeconds(60); // Set TTL to 60 seconds

        return operations.opsForList()
                .rightPush("users", user)
                .flatMap(result -> operations.expire("users", ttl))
                .map(result -> "List set with TTL");
    }

    public Mono<Long> publishUser(User user) {
        user.setId(UUID.randomUUID().toString());
        return this.operations.convertAndSend("users", user);
    }
}
