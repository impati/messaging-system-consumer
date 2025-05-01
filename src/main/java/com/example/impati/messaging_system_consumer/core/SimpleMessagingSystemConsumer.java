package com.example.impati.messaging_system_consumer.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SimpleMessagingSystemConsumer<T> implements MessagingSystemConsumer<T> {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final JavaType dataType;

    public SimpleMessagingSystemConsumer(WebClient.Builder webClientBuilder,
                                         MessagingSystemProperties properties,
                                         ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(properties.url()).build();
        this.dataType = TypeFactory.defaultInstance().constructType(
                ResolvableType.forClass(SimpleMessagingSystemConsumer.class, getClass())
                        .getGeneric(0)
                        .getType());
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
                                .map(msg -> objectMapper.convertValue(msg, dataType))
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
