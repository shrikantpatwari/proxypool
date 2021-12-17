package com.woo.proxypool.service.impl;

import com.woo.proxypool.service.api.KafKaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafKaProducerServiceImpl implements KafKaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafKaProducerServiceImpl.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String message) {
        logger.info(String.format("%s message is sent -> to %s Topic ", message, topic));
        this.kafkaTemplate.send(topic, message);
    }
}
