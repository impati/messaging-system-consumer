package com.example.impati.messaging_system_consumer.core;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class SimpleMessagingSystemPoller<T> implements MessagingSystemPoller {

    private final MessagingSystemConsumer<T> messagingSystemConsumer;
    private final MessagingSystemListener<T> messagingSystemListener;

    public SimpleMessagingSystemPoller(MessagingSystemConsumer<T> messagingSystemConsumer, MessagingSystemListener<T> messagingSystemListener) {
        this.messagingSystemConsumer = messagingSystemConsumer;
        this.messagingSystemListener = messagingSystemListener;
    }

    public void listen(Channel channel) {
        Flux.interval(Duration.ofMillis(100))
                .flatMap(tick -> messagingSystemConsumer.consume(channel))
                .subscribe(message -> message.forEach(messagingSystemListener::listen));
    }
}
