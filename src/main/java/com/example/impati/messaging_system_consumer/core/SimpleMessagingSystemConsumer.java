package com.example.impati.messaging_system_consumer.core;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class SimpleMessagingSystemConsumer implements MessagingSystemConsumer {

    private final WebClient webClient;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
    }

    @Override
    public <T> Flux<T> consume(Channel channel, Class<T> bodyType) {
        String consumerId = Client.getInstance().getConsumerId(channel);

        ParameterizedTypeReference<MessageResponses<T>> typeRef = new ParameterizedTypeReference<>() {
        };

        return webClient.get()
                .uri("/v1/consume/{id}", consumerId)
                .retrieve()
                .bodyToMono(typeRef)
                .flatMapIterable(MessageResponses::messages)
                .map(MessageResponse::data);
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
