package com.example.impati.messaging_system_consumer.core;

public interface MessagingSystemListener<T> {

    void listen(T data);
}
