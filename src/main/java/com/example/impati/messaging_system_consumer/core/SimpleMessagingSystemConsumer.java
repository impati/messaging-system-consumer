package com.example.impati.messaging_system_consumer.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final Subscription subscription;
    private final WebClient webClient;

    public SimpleMessagingSystemConsumer(Subscription subscription, WebClient webClient) {
        this.subscription = subscription;
        this.webClient = webClient;
    }

    @SuppressWarnings("unchecked")
    public Mono<List<T>> consume(Channel channel) {
        String consumerId = subscription.consumerId(channel);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/consume/{consumerId}").build(consumerId))
                .retrieve()
                .bodyToMono(MessageResponses.class)
                .map(responses ->
                        responses.messages().stream()
                                .map(MessageResponse::data)
                                .map(data -> (T) data)
                                .toList());
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
