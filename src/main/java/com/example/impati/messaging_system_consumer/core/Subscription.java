package com.example.impati.messaging_system_consumer.core;

import java.util.HashMap;
import java.util.Map;

public class Subscription {

    private final Map<Channel, String> store = new HashMap<>();

    public void add(Channel channel, String consumerId) {
        store.put(channel, consumerId);
    }

    public String consumerId(Channel channel) {
        return store.get(channel);
    }
}
