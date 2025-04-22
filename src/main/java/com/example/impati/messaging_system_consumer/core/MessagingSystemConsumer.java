package com.example.impati.messaging_system_consumer.core;

import java.util.List;
import reactor.core.publisher.Mono;

public interface MessagingSystemConsumer<T> {

    Mono<List<T>> consume(Channel channel);
}
