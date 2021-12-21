package com.woo.proxypool.service.impl;

import com.woo.proxypool.service.api.KafkaConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {
    private final Logger logger =
            LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    @Override
    @KafkaListener(topics = "seconds", groupId = "groupId")
    public void consumeSeconds(String message) {
        logger.info(String.format("Message received in seconds queue -> %s", message));
    }

    @Override
    @KafkaListener(topics = "minutes", groupId = "groupId")
    public void consumeMinutes(String message) {
        logger.info(String.format("Message received in minutes queue -> %s", message));
    }

    @Override
    @KafkaListener(topics = "day", groupId = "groupId")
    public void consumeDay(String message) {
        logger.info(String.format("Message received in day queue -> %s", message));
    }
}
