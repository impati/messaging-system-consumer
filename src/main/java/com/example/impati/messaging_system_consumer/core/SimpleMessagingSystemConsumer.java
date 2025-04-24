package com.example.impati.messaging_system_consumer.core;

import com.example.impati.messaging_system_consumer.config.Properties;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final Subscription subscription;
    private final Properties properties;
    private final WebClient webClient;
    private final Function<JsonNode, T> converter;

    public SimpleMessagingSystemConsumer(Subscription subscription,
                                         Properties properties,
                                         WebClient webClient,
                                         Function<JsonNode, T> converter) {
        this.subscription = subscription;
        this.properties = properties;
        this.webClient = webClient;
        this.converter = converter;
    }

    @SuppressWarnings("unchecked")
    public Mono<List<T>> consume(Channel channel) {
        String consumerId = subscription.consumerId(channel);
        return webClient.get()
                .uri(properties.url() + "/v1/consume/" + consumerId)
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
