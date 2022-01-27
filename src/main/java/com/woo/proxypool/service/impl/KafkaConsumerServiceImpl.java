package com.woo.proxypool.service.impl;

import com.bugsnag.Bugsnag;
import com.woo.proxypool.service.api.KafkaConsumerService;
import com.woo.proxypool.service.api.ProxyPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Autowired
    Bugsnag bugsnag;

    private final ProxyPoolService proxyPoolService;


    @Override
    @KafkaListener(topics = "Bot_Updates", groupId = "groupId")
    public void consumeTopicMessage(String message) {
        log.info(String.format("Message received in backoffice queue -> %s", message));
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.get("Type").equals("NewBotActivated")) {
                proxyPoolService.assignProxyToUser(jsonObject);
            }
            if (jsonObject.get("Type").equals("BotProxyResponse")) {
                proxyPoolService.assignNewProxy(jsonObject);
            }
        } catch (JSONException je) {
            log.error(je.getMessage(), je);
            bugsnag.notify(je);
        }
    }
}
