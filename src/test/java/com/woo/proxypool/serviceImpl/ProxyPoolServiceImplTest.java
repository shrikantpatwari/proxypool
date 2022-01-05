package com.woo.proxypool.serviceImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.impl.ProxyPoolServiceImpl;
import com.woo.proxypool.util.RateLimitingQueue;
import com.woo.proxypool.util.WooConstants;

@ExtendWith(MockitoExtension.class)
public class ProxyPoolServiceImplTest {
    @Mock
    ProxyListRepository proxyListRepository;
    private ProxyPoolServiceImpl proxyPoolServiceImpl = new ProxyPoolServiceImpl();
    private RateLimitingQueue rateLimitingQueue = RateLimitingQueue.getInstance();
    @Mock
    private RateLimitingQueue rateLimitingQueue1;

    // @Mock
    // private WooConstants wooConstants ;

    private ArrayList<Long> secondsQueue = null;
    private ArrayList<Long> minutesQueue = null;
    private ArrayList<Long> dayQueue = null;

    // private WooConstants wooConstants = new WooConstants() ;
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        // ReflectionTestUtils.setField(WooConstants.SECONDS_QUEUE_LIMIT,
        // "SECONDS_QUEUE_LIMIT",1);
        ReflectionTestUtils.setField(proxyPoolServiceImpl, "proxyListRepository", proxyListRepository);
    }

    @Test
    void getAProxy_null() {
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertEquals(proxy, null);
    }

    @Test
    void getAProxy_1() {
        secondsQueue = new ArrayList<Long>(
                Arrays.asList(9991640325764990l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l,
                        15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l,
                        10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l));
        minutesQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        dayQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));

        ReflectionTestUtils.setField(rateLimitingQueue, "secondsQueue", secondsQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "minutesQueue", minutesQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "dayQueue", dayQueue);

        ProxyList proxyList = new ProxyList();
        proxyList.setId(1l);
        proxyList.setCreatedAt(new Date());
        proxyList.setIp("192.168.0.1");
        proxyList.setStatus(2);
        proxyList.setUpdatedAt(new Date());
        // rateLimitingQueue.initQueues();
        when(proxyListRepository.findFirstByStatus(2)).thenReturn(proxyList);

        /*
         * HashMap<String, Integer> queueMap = new HashMap<String, Integer>() {{
         * put("secondsQueue", secondsQueue.size()); put("minutesQueue",
         * minutesQueue.size()); put("dayQueue", dayQueue.size()); }};
         *
         * when(rateLimitingQueue1.getCountOfItemsInAllQueues()).thenReturn(queueMap);
         */
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertNotNull(proxy);
    }

    @Test
    void getAProxy_11() {
        secondsQueue = new ArrayList<Long>(Arrays.asList(90l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l,
                10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l,
                15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l));
        minutesQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        dayQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));

        ReflectionTestUtils.setField(rateLimitingQueue, "secondsQueue", secondsQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "minutesQueue", minutesQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "dayQueue", dayQueue);

        ProxyList proxyList = new ProxyList();
        proxyList.setId(1l);
        proxyList.setCreatedAt(new Date());
        proxyList.setIp("192.168.0.1");
        proxyList.setStatus(2);
        proxyList.setUpdatedAt(new Date());
        // rateLimitingQueue.initQueues();
        when(proxyListRepository.findFirstByStatus(2)).thenReturn(proxyList);

        /*
         * HashMap<String, Integer> queueMap = new HashMap<String, Integer>() {{
         * put("secondsQueue", secondsQueue.size()); put("minutesQueue",
         * minutesQueue.size()); put("dayQueue", dayQueue.size()); }};
         *
         * when(rateLimitingQueue1.getCountOfItemsInAllQueues()).thenReturn(queueMap);
         */
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertNotNull(proxy);
    }

    @Test
    void getAProxy() {
        secondsQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        minutesQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        dayQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));

        ReflectionTestUtils.setField(rateLimitingQueue, "secondsQueue", secondsQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "minutesQueue", minutesQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "dayQueue", dayQueue);

        ProxyList proxyList = new ProxyList();
        proxyList.setId(1l);
        proxyList.setCreatedAt(new Date());
        proxyList.setIp("192.168.0.1");
        proxyList.setStatus(2);
        proxyList.setUpdatedAt(new Date());
        when(proxyListRepository.findFirstByStatus(2)).thenReturn(proxyList);
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertNotNull(proxy);
    }

    @Test
    void getAProxy_2() {
        secondsQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        minutesQueue = new ArrayList<Long>(Arrays.asList(10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l,
                10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l,
                15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l));
        dayQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));

        ReflectionTestUtils.setField(rateLimitingQueue, "secondsQueue", secondsQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "minutesQueue", minutesQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "dayQueue", dayQueue);

        ProxyList proxyList = new ProxyList();
        proxyList.setId(1l);
        proxyList.setCreatedAt(new Date());
        proxyList.setIp("192.168.0.1");
        proxyList.setStatus(2);
        proxyList.setUpdatedAt(new Date());
        when(proxyListRepository.findFirstByStatus(2)).thenReturn(proxyList);
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertNotNull(proxy);
    }

    @Test
    void getAProxy_3() {
        // WooConstants constant = Mockito.mock(WooConstants.class) ;
        /// Whitebox.setInternalState(WooConstants.class, "DAY_QUEUE_LIMIT", constant);
        /*
         * try { setFinalStatic(WooConstants.class.getField("SECONDS_QUEUE_LIMIT"),
         * 177); } catch (Exception e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        // when(WooConstants.getDayQueueLimit()).thenReturn(10);
        secondsQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));
        dayQueue = new ArrayList<Long>(Arrays.asList(10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l,
                15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l,
                10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l, 10l, 15l));
        minutesQueue = new ArrayList<Long>(Arrays.asList(10l, 15l));

        ReflectionTestUtils.setField(rateLimitingQueue, "secondsQueue", secondsQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "minutesQueue", minutesQueue);
        ReflectionTestUtils.setField(rateLimitingQueue, "dayQueue", dayQueue);

        ProxyList proxyList = new ProxyList();
        proxyList.setId(1l);
        proxyList.setCreatedAt(new Date());
        proxyList.setIp("192.168.0.1");
        proxyList.setStatus(2);
        proxyList.setUpdatedAt(new Date());
        when(proxyListRepository.findFirstByStatus(2)).thenReturn(proxyList);
        String proxy = proxyPoolServiceImpl.getAProxy();
        assertNotNull(proxy);
    }

    void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        field.set(null, newValue);
        // remove final modifier from field
        Field modifiersField = WooConstants.class.getDeclaredField("SECONDS_QUEUE_LIMIT");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

}
