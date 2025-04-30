package com.example.impati.messaging_system_consumer.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final WebClient webClient;
    private final ObjectMapper mapper;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties,
                                         ObjectMapper mapper) {
        this.mapper = mapper;
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
    }

    @SuppressWarnings("unchecked")
    public Mono<List<T>> consume(Channel channel) {
        String consumerId = Client.getInstance().getConsumerId(channel);
        return webClient.get()
                .uri("/v1/consume/{id}", consumerId)
                .retrieve()
                .bodyToMono(MessageResponses.class)
                .map(responses ->
                        responses.messages().stream()
                                .map(data -> mapper.convertValue(data, new TypeReference<T>() {
                                }))
                                .toList());
    }

    record MessageResponses<T>(
            List<MessageResponse<T>> messages
    ) {

    }

    record MessageResponse<T>(
            LocalDateTime createdAt,

            T data
    ) {

    }
}
