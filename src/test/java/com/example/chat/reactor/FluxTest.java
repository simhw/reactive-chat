package com.example.chat.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxTest {
    @Test
    void iterable() {
        Flux.fromIterable(List.of("foo", "bar"))
                .doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    void subscribe() {
        Flux.fromIterable(List.of("foo", "bar"))
                .doOnNext(System.out::println)
                .map(String::toUpperCase)
                .doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    void error() {
        Flux.error(new RuntimeException())
                .doOnError(System.err::println)
                .subscribe();
    }

    @Test
    void interval() throws InterruptedException {
        System.out.println("start");

        Flux.interval(Duration.ofMillis(100))
                .take(10)
                .subscribe(System.out::println);

        Thread.sleep(1000);

        System.out.println("end");
    }

    @Test
    void flatMap(){
        /**
         * "a", "b", "c"
         * "d", "e", "f"
         * "g", "h", "i"
         * "j", "k"
         */

        Flux.fromIterable(
                List.of(Flux.just("a", "b", "c"),
                        Flux.just("d", "e", "f"),
                        Flux.just("g", "h", "i"),
                        Flux.just("j", "k")
                ))
        .flatMap(v -> v.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void concatMap() {
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")
                .window(3)
                .concatMap(values -> values.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void flatMapSequential() {
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")
                .window(3)
                .flatMapSequential(values -> values.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    List<String> toUpperCase(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return List.of(s.toUpperCase(), Thread.currentThread().getName());
    }
}
