package com.example.chat.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.spec.PSource;
import java.time.Duration;
import java.util.List;

public class MonoTest {
    @Test
    void or() {
        Mono.delay(Duration.ofSeconds(1))
                .or(Mono.just(2L))
                .subscribe(System.out::println);
    }

    @Test
    void empty(){
        Mono.empty()
                .subscribe(System.out::println);

        Mono.never()
                .subscribe(System.out::println);
    }

}
