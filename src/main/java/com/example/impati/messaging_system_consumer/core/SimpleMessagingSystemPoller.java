package com.example.impati.messaging_system_consumer.core;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class SimpleMessagingSystemPoller implements MessagingSystemPoller {

    private final MessagingSystemConsumer messagingSystemConsumer;
    private final MessagingSystemListener messagingSystemListener;
    private final ChannelRegistration channelRegistration;

    public SimpleMessagingSystemPoller(MessagingSystemConsumer messagingSystemConsumer,
                                       MessagingSystemListener messagingSystemListener,
                                       ChannelRegistration channelRegistration) {
        this.messagingSystemConsumer = messagingSystemConsumer;
        this.messagingSystemListener = messagingSystemListener;
        this.channelRegistration = channelRegistration;
    }

    public void listen(Channel channel) {
        Class<?> type = channelRegistration.typeFor(channel);
        Flux.interval(Duration.ofMillis(100))
                .flatMap(tick -> messagingSystemConsumer.consume(channel, type))
                .subscribe(messagingSystemListener::listen);
    }
}
