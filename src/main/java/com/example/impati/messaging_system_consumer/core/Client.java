package com.example.impati.messaging_system_consumer.core;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private static final Client INSTANCE = new Client();

    private String clientId;
    private final Map<Channel, String> consumer;

    private Client() {
        consumer = new HashMap<>();
    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void addConsumer(Channel channel, String consumerId) {
        consumer.put(channel, consumerId);
    }

    public String getConsumerId(Channel channel) {
        return consumer.get(channel);
    }
}
