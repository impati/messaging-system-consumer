package com.example.impati.messaging_system_consumer.core;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class SimpleChannelSubscriber implements ChannelSubscriber {

    private final WebClient webClient;

    public SimpleChannelSubscriber(WebClient.Builder webClientBuilder, MessagingSystemProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
    }

    public void subscribe(Client client, List<Channel> channels) {
        for (Channel channel : channels) {
            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/channels/" + channel.name() + "/message-subscribe")
                            .queryParam("clientId", client.getClientId())
                            .build())
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return webClient.post()
                                    .uri(uriBuilder -> uriBuilder
                                            .path("/v1/channels/" + channel.name() + "/message-subscribe")
                                            .build())
                                    .bodyValue(new SubscribeRequest(client.getClientId()))
                                    .retrieve()
                                    .bodyToMono(SubscribeResponse.class);
                        }

                        return clientResponse.bodyToMono(SubscribeResponse.class);
                    })
                    .doOnNext(response -> client.addConsumer(channel, response.consumerId))
                    .subscribe();
        }
    }

    record SubscribeRequest(String clientId) {

    }

    record SubscribeResponse(String consumerId, String clientId) {

    }
}
