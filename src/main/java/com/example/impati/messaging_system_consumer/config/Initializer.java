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

    public Initializer(WebClient client, Properties properties, ChannelBuilder channelBuilder) {
        this.client = client;
        this.properties = properties;
        this.channelBuilder = channelBuilder;
    }

    public void initialize() {

        // 클라이언트 등록
        // clientId 를 저장하고 있어야함.
        client.get()
                .uri(properties.url() + "/v1/client?clientName=" + properties.clientName())
                .exchangeToMono(clientResponse ->{
                    if(clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return client.post()
                                .uri(properties.url() + "/v1/client?clientName=" + properties.clientName())
                                .retrieve()
                                .bodyToMono(String.class);
                    }

                    return clientResponse.bodyToMono(String.class);
                })
                .subscribe(clientId -> Client.clientId = clientId);

        // 채널을 구독
        // consumerId 를 저장하고 있어야함.
        ChannelProvider channelProvider = channelBuilder.build();
        List<Channel> channels = channelProvider.getAll();
        Subscription subscription = new Subscription();
        for(Channel channel : channels) {
            client.get()
                    .uri("/v1/channels/" + channel.name() + "/message-subscribe?clientId=" + Client.clientId)
                    .exchangeToMono(clientResponse -> {
                        if(clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return client.post()
                                    .uri("/v1/channels/" + channel.name() + "/message-subscribe?clientId=" + Client.clientId)
                                    .retrieve()
                                    .bodyToMono(SubscribeResponse.class);
                        }

                        return clientResponse.bodyToMono(SubscribeResponse.class);
                    })
                    .subscribe(response -> subscription.add(channel,response.consumerId));
        }
    }

    record SubscribeResponse(String consumerId, String clientId) {

    }
}
