package com.woo.proxypool.service.api;

public interface KafKaProducerService {
    void sendTopicMessage(String topic, String message);
}
