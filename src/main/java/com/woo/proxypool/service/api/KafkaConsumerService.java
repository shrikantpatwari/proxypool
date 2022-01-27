package com.woo.proxypool.service.api;

public interface KafkaConsumerService {
    void consumeTopicMessage(String message);
}
