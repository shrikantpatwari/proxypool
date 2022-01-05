package com.woo.proxypool.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.impl.ProxyPoolServiceImpl;

@ExtendWith(MockitoExtension.class)
public class IPPoolManagerTests {
    @InjectMocks
    ProxyPoolServiceImpl proxyPoolService;

    @Mock
    ApplicationContext context;

    private IPPoolManager iPPoolManager = IPPoolManager.getInstance(context);

    @Mock
    ProxyListRepository proxyListRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(iPPoolManager, "proxyPoolService", proxyPoolService);
    }

    @Test
    void getAndCreateIPPool() {
        MockitoAnnotations.openMocks(this);
        // when(proxyListRepository) proxyPoolServiceImpl.
        when(proxyPoolService.getCountOfDBAvailableIP()).thenReturn(10l);
        iPPoolManager.getAndCreateIPPool();
        // verify(proxyPoolService).getCountOfDBAvailableIP() ;
        assertNotNull(proxyPoolService.getThirdPartyProxyList());
    }

    @Test
    void getAndCreateIPPool_withZeroSize() {
        MockitoAnnotations.openMocks(this);
        // when(proxyListRepository) proxyPoolServiceImpl.
        when(proxyPoolService.getCountOfDBAvailableIP()).thenReturn(0l);
        iPPoolManager.getAndCreateIPPool();
        // verify(proxyPoolService).getCountOfDBAvailableIP() ;
        assertNotNull(proxyPoolService.getThirdPartyProxyList());
    }
}
