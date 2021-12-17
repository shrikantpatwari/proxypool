package com.woo.proxypool.service.api;

public interface KafKaProducerService {
    void sendMessage(String topic, String message);
}
