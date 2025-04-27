package com.example.impati.messaging_system_consumer.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class SimpleClientRegister implements ClientRegister {

    private final WebClient client;

    public SimpleClientRegister(WebClient.Builder webClientBuilder, MessagingSystemProperties properties) {
        this.client = webClientBuilder.baseUrl(properties.url()).build();
    }

    public Client register(String clientName) {
        Client instance = Client.getInstance();
        instance.setClientId(client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/client")
                        .queryParam("clientName", clientName)
                        .build())
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return client.post()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/v1/client")
                                        .queryParam("clientName", clientName)
                                        .build())
                                .retrieve()
                                .bodyToMono(String.class);
                    }

                    return response.bodyToMono(String.class);
                })
                .block());

        return instance;
    }
}
