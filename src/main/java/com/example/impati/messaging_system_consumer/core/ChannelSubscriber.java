package com.example.impati.messaging_system_consumer.core;

import java.util.List;

public interface ChannelSubscriber {

    void subscribe(Client client, List<Channel> channels);
}
