package com.woo.proxypool.service.impl;

import com.woo.proxypool.service.api.KafKaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafKaProducerServiceImpl implements KafKaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendTopicMessage(String topic, String message) {
        log.info(String.format("%s message is sent -> to %s Topic ", message, topic));
        this.kafkaTemplate.send(topic, message);
    }
}
