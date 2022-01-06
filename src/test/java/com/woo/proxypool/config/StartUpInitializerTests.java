package com.woo.proxypool.config;

import com.woo.proxypool.service.api.ProxyPoolService;
import com.woo.proxypool.util.RateLimitingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.impl.ProxyPoolServiceImpl;
import com.woo.proxypool.util.IPPoolManager;

@ExtendWith(MockitoExtension.class)
class StartUpInitializerTests {
    @Mock
    ApplicationContext context;

    @Mock
    private ProxyListRepository proxyListRepository;

    private IPPoolManager iPPoolManager;

    private RateLimitingQueue rateLimitingQueue;

    private ProxyPoolService proxyPoolService;

    @BeforeEach
    public void setUp() {
        proxyPoolService = new ProxyPoolServiceImpl(proxyListRepository);
        rateLimitingQueue = RateLimitingQueue.getInstance();
        iPPoolManager = IPPoolManager.getInstance(context);
    }

    @Test
    void handleContextRefresh() {

        Mockito.when(proxyPoolService.getCountOfDBAvailableIP()).thenReturn(10l);

    }
}
