package com.example.impati.messaging_system_consumer.core;

import reactor.core.publisher.Flux;

public interface MessagingSystemConsumer<T> {

    Flux<T> consume(Channel channel, Class<T> bodyType);
}
