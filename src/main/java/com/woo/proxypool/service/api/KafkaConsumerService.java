package com.woo.proxypool.service.api;

public interface KafkaConsumerService {
    void consumeSeconds(String message);
    void consumeMinutes(String message);
    void consumeDay(String message);
}
