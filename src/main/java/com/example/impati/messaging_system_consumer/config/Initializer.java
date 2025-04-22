package com.example.impati.messaging_system_consumer.config;

import com.example.impati.messaging_system_consumer.core.Channel;
import com.example.impati.messaging_system_consumer.core.ChannelProvider;
import com.example.impati.messaging_system_consumer.core.Client;
import com.example.impati.messaging_system_consumer.core.Subscription;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class Initializer {

    private final WebClient client;
    private final Properties properties;
    private final ChannelBuilder channelBuilder;
    private final Subscription subscription;

    public Initializer(WebClient client, Properties properties, ChannelBuilder channelBuilder, Subscription subscription) {
        this.client = client;
        this.properties = properties;
        this.channelBuilder = channelBuilder;
        this.subscription = subscription;
    }

    public void initialize() {
        System.out.println("initialize");

        // 클라이언트 등록
        // clientId 를 저장하고 있어야함.

        Client.clientId = client.get()
                .uri(properties.url() + "/v1/client?clientName=" + properties.clientName())
                .exchangeToMono(clientResponse1 -> {
                    if (clientResponse1.statusCode() == HttpStatus.NOT_FOUND) {
                        return client.post()
                                .uri(properties.url() + "/v1/client?clientName=" + properties.clientName())
                                .retrieve()
                                .bodyToMono(String.class);
                    }

                    return clientResponse1.bodyToMono(String.class);
                })
                .block();
        System.out.println("clientId = " + Client.clientId);

        // 채널을 구독
        // consumerId 를 저장하고 있어야함.
        ChannelProvider channelProvider = channelBuilder.build();
        List<Channel> channels = channelProvider.getAll();
        for (Channel channel : channels) {
            client.get()
                    .uri(properties.url() + "/v1/channels/" + channel.name() + "/message-subscribe?clientId=" + Client.clientId)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return client.post()
                                    .uri(properties.url() + "/v1/channels/" + channel.name() + "/message-subscribe?clientId=" + Client.clientId)
                                    .retrieve()
                                    .bodyToMono(SubscribeResponse.class);
                        }

                        return clientResponse.bodyToMono(SubscribeResponse.class);
                    })
                    .doOnNext(response -> subscription.add(channel, response.consumerId))
                    .subscribe();
        }
    }

    record SubscribeResponse(String consumerId, String clientId) {

    }
}
