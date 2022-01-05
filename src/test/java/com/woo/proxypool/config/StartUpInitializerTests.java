package com.woo.proxypool.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.util.ReflectionTestUtils;

import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.impl.ProxyPoolServiceImpl;
import com.woo.proxypool.util.IPPoolManager;

@ExtendWith(MockitoExtension.class)
class StartUpInitializerTests {
    @Mock
    private AnnotationConfigApplicationContext source;

    @Mock
    ApplicationContext context;

    @Mock
    private ProxyListRepository proxyListRepository;

    @InjectMocks
    ProxyPoolServiceImpl proxyPoolService;

    private IPPoolManager iPPoolManager = IPPoolManager.getInstance(context);

    // @MockBean
    private ProxyPoolServiceImpl proxyPoolServiceImpl = new ProxyPoolServiceImpl();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(proxyPoolServiceImpl, "proxyListRepository", proxyListRepository);
        ReflectionTestUtils.setField(iPPoolManager, "proxyPoolService", proxyPoolService);
        // MockitoAnnotations.openMocks(source) ;//initMocks(this);
        // startUpInitializer = mock(StartUpInitializer.class) ;
        // source = mock(ApplicationContext.class);
    }

    @Test
    void handleContextRefresh() {
        // MockitoAnnotations.openMocks(this);
        // MockitoAnnotations.openMocks(proxyPoolService);
        StartUpInitializer startUpInitializer = new StartUpInitializer();
        Mockito.when(proxyPoolServiceImpl.getCountOfDBAvailableIP()).thenReturn(10l);
        startUpInitializer.handleContextRefresh(new ContextRefreshedEvent(source));
        System.out.println();
    }
}
