package com.example.impati.messaging_system_consumer.core;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final WebClient webClient;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
    }

    @Override
    public Flux<T> consume(final Channel channel, final Class<T> bodyType) {
        String consumerId = Client.getInstance().getConsumerId(channel);

        ResolvableType resolvableType = ResolvableType
                .forClassWithGenerics(MessageResponses.class, bodyType);

        ParameterizedTypeReference<MessageResponses<T>> typeRef =
                ParameterizedTypeReference.forType(resolvableType.getType());

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
