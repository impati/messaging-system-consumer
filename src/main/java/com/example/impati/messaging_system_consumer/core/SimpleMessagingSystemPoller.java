package com.example.impati.messaging_system_consumer.core;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class SimpleMessagingSystemPoller<T> implements MessagingSystemPoller {

    private final MessagingSystemConsumer<T> messagingSystemConsumer;
    private final MessagingSystemListener<T> messagingSystemListener;
    private final ChannelRegistration channelRegistration;

    public SimpleMessagingSystemPoller(MessagingSystemConsumer<T> messagingSystemConsumer,
                                       MessagingSystemListener<T> messagingSystemListener,
                                       ChannelRegistration channelRegistration) {
        this.messagingSystemConsumer = messagingSystemConsumer;
        this.messagingSystemListener = messagingSystemListener;
        this.channelRegistration = channelRegistration;
    }

    @SuppressWarnings("unchecked")
    public void listen(Channel channel) {
        Class<T> type = (Class<T>) channelRegistration.typeFor(channel);
        Flux.interval(Duration.ofMillis(100))
                .flatMap(tick -> messagingSystemConsumer.consume(channel, type))
                .subscribe(messagingSystemListener::listen);
    }
}
