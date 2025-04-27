package com.example.impati.messaging_system_consumer.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final WebClient webClient;
    private final Function<JsonNode, T> converter;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties,
                                         Function<JsonNode, T> converter) {
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
        this.converter = converter;
    }

    public Mono<List<T>> consume(Channel channel) {
        Client instance = Client.getInstance();
        String consumerId = instance.getConsumerId(channel);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/consume/" + consumerId)
                        .build())
                .retrieve()
                .bodyToMono(MessageResponses.class)
                .map(responses ->
                        responses.messages().stream()
                                .map(MessageResponse::data)
                                .map(converter)
                                .toList()
                );
    }

    record MessageResponses(
            List<MessageResponse> messages
    ) {

    }

    record MessageResponse(
            LocalDateTime createdAt,

            JsonNode data
    ) {

    }
}
