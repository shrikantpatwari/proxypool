package com.woo.proxypool.config;

import com.woo.proxypool.util.IPPoolManager;
import com.woo.proxypool.util.RateLimitingQueue;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartUpInitializer {
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        RateLimitingQueue.getInstance().initQueues();
        IPPoolManager.getInstance().getAndCreateIPPool();
    }
}
