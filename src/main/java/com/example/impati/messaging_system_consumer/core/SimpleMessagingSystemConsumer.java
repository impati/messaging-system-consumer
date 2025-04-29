package com.example.impati.messaging_system_consumer.core;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final WebClient webClient;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
    }

    public Mono<List<T>> consume(Channel channel) {
        String consumerId = Client.getInstance().getConsumerId(channel);
        return webClient.get()
                .uri("/v1/consume/{id}", consumerId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MessageResponses<T>>() {
                })
                .flatMapIterable(MessageResponses::messages)
                .map(MessageResponse::data)
                .collectList();
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
