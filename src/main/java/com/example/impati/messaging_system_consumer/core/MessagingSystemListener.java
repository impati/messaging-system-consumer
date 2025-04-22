package com.example.impati.messaging_system_consumer.core;

import java.time.Duration;
import java.util.function.Consumer;
import reactor.core.publisher.Flux;

public class MessagingSystemListener<T> {

    private final MessagingSystemConsumer<T> messagingSystemConsumer;
    private final Consumer<T> consumer;

    public MessagingSystemListener(MessagingSystemConsumer<T> messagingSystemConsumer, Consumer<T> consumer) {
        this.messagingSystemConsumer = messagingSystemConsumer;
        this.consumer = consumer;
    }

    public void listen(String channelName) {
        Flux.interval(Duration.ofMillis(100))
                .flatMap(tick -> messagingSystemConsumer.consume(new Channel(channelName)))
                .subscribe(message -> message.forEach(consumer));
    }
}
