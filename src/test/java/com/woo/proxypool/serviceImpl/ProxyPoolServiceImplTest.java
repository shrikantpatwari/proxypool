package com.woo.proxypool.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.woo.proxypool.service.api.ProxyPoolService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.impl.ProxyPoolServiceImpl;
import com.woo.proxypool.util.RateLimitingQueue;
import com.woo.proxypool.util.WooConstants;

@ExtendWith(MockitoExtension.class)
public class ProxyPoolServiceImplTest {
    @Mock
    private ProxyListRepository proxyListRepository;

    private ProxyPoolService proxyPoolService;

    private RateLimitingQueue rateLimitingQueue;

    @BeforeEach
    public void init() {
        proxyPoolService = new ProxyPoolServiceImpl(proxyListRepository);
        rateLimitingQueue = RateLimitingQueue.getInstance();
        rateLimitingQueue.initQueues();
    }

    /**
     * Test Should return Null If No Proxy With InUse Or Ready Status
     */
    @Test
    @DisplayName("getReadyOrInUserIPFromDB method should return Null If No Proxy With InUse Or Ready Status")
    void shouldBeNull_GetReadyOrInUserIPFromDB() {
        when(proxyListRepository.findFirstByStatus(WooConstants.IN_USE)).thenReturn(null);
        when(proxyListRepository.findFirstByStatus(WooConstants.READY)).thenReturn(null);
        assertNull(proxyPoolService.getReadyOrInUserIPFromDB());
    }

    /**
     * Test should return valid proxy for first call with below check
     * # All queues should be initialised as empty array list
     * # Provided READY status proxy should change status to In_Use
     * # Method should return valid ProxyList Object
     */
    @Test
    @DisplayName("getReadyOrInUserIPFromDB method should return valid proxy for first call if there is proxy with ready status")
    void shouldBeValidProxy_GetReadyOrInUserIPFromDB() {
        ProxyList proxyList = new ProxyList(1L, "103.53.76.82:8089", WooConstants.READY, new Date(), new Date());
        when(proxyListRepository.findFirstByStatus(WooConstants.IN_USE)).thenReturn(null);
        when(proxyListRepository.findFirstByStatus(WooConstants.READY)).thenReturn(proxyList);
        ProxyList expectedProxy = proxyPoolService.getReadyOrInUserIPFromDB();
        HashMap<String, ArrayList<Long>> rateLimitingQueues = rateLimitingQueue.getAllQueues();
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        Assertions.assertNotNull(rateLimitingQueues.get("secondsQueue"));
        Assertions.assertNotNull(rateLimitingQueues.get("minutesQueue"));
        Assertions.assertNotNull(rateLimitingQueues.get("dayQueue"));
        assertEquals(queuesCount.get("secondsQueue"), 0);
        assertEquals(queuesCount.get("minutesQueue"), 0);
        assertEquals(queuesCount.get("dayQueue"), 0);
        assertEquals(expectedProxy.getStatus(), WooConstants.IN_USE);
        assertEquals(expectedProxy.getIp(), proxyList.getIp());
    }

    /**
     * Test Method call should return false if current call is first call to method
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for first call")
    void shouldBeFalse_CheckIfLimitsExhausted_If_FirstCall() {
        Long currentTime = new Date().getTime();
        Boolean isExhausted = proxyPoolService.checkIfLimitsExhausted(currentTime);
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 1);
        assertEquals(queuesCount.get("minutesQueue"), 1);
        assertEquals(queuesCount.get("dayQueue"), 1);
        assertEquals(isExhausted, false);
    }

    /**
     * Test Method call should return false if
     * # calls made are less than seconds queue call limit (49) and within seconds time limit (1 second)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than seconds queue call limit (49) and within seconds time limit (1 second)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_SecondsQueue_CallLimit_And_LessThan_SecondsQueue_TimeLimit() {
        Long currentTime = new Date().getTime();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        for (int i = 0; i < WooConstants.SECONDS_QUEUE_LIMIT; i++) {
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentTime));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 49);
        assertEquals(queuesCount.get("dayQueue"), 49);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return false if
     * # calls made are less than seconds queue call limit (49) and more than seconds time limit (1 second)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than seconds queue call limit (49) and more than seconds time limit (1 second)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_SecondsQueue_CallLimit_And_MoreThan_SecondsQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 2000);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 11);
        assertEquals(queuesCount.get("minutesQueue"), 11);
        assertEquals(queuesCount.get("dayQueue"), 11);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return true if
     * # calls made are more than seconds queue call limit (49) and within seconds time limit (1 second)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return true for calls made are more than seconds queue call limit (49) and less than seconds time limit (1 second)")
    void shouldBeTrue_CheckIfLimitsExhausted_If_Calls_MoreThan_SecondsQueue_CallLimit_And_LessThan_SecondQueue_TimeLimit() {
        Long currentTime = new Date().getTime();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        for (int i = 0; i < WooConstants.SECONDS_QUEUE_LIMIT + 1; i++) {
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentTime));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 0);
        assertEquals(queuesCount.get("minutesQueue"), 0);
        assertEquals(queuesCount.get("dayQueue"), 0);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), true);
    }

    /**
     * Test Method call should return false if
     * # calls made are more than seconds queue call limit (49) and more than seconds time limit (1 second)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are more than seconds queue call limit (49) and more than seconds time limit (1 second)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_MoreThan_SecondsQueue_CallLimit_And_MoreThan_SecondQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        for (int i = 0; i < WooConstants.SECONDS_QUEUE_LIMIT; i++) {
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 2000);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 50);
        assertEquals(queuesCount.get("dayQueue"), 50);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return false if
     * # calls made not exhausting in seconds queue limits and
     * # calls made are less than Minutes queue call limit (1199) and within Minutes time limit (1 Minute)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than Minutes queue call limit (1199) and less than Minutes time limit (1 Minute)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_MinutesQueue_CallLimit_And_LessThan_MinutesQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < WooConstants.MINUTES_QUEUE_LIMIT - 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * count) {
                count++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1198);
        assertEquals(queuesCount.get("dayQueue"), 1198);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return false if
     * # calls made not exhausting in seconds queue limits and
     * # calls made are less than Minutes queue call limit (1299) and more than Minutes time limit (1 Minute)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than Minutes queue call limit (1199) and more than Minutes time limit (1 Minute)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_MinutesQueue_CallLimit_And_MoreThan_MinutesQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < WooConstants.MINUTES_QUEUE_LIMIT - 2; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * count) {
                count++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 61000);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1198);
        assertEquals(queuesCount.get("dayQueue"), 1198);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return true if
     * # calls made not exhausting in seconds queue limits and
     * # calls made are more than Minutes queue call limit (1299) and within Minutes time limit (1 Minute)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return true for calls made are more than Minutes queue call limit (1199) and less than Minutes time limit (1 Minute)")
    void shouldBeTrue_CheckIfLimitsExhausted_If_Calls_MoreThan_MinutesQueue_CallLimit_And_LessThan_MinutesQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < WooConstants.MINUTES_QUEUE_LIMIT + 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * count) {
                count++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 0);
        assertEquals(queuesCount.get("minutesQueue"), 0);
        assertEquals(queuesCount.get("dayQueue"), 0);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), true);
    }

    /**
     * Test Method call should return false if
     * # calls made not exhausting in seconds queue limits and
     * # calls made are more than Minutes queue call limit (1199) and more than Minutes time limit (1 Minute)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are more than Minutes queue call limit (1199) and more than Minutes time limit (1 Minute)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_MoreThan_MinutesQueue_CallLimit_And_MoreThan_MinutesQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < WooConstants.MINUTES_QUEUE_LIMIT - 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * count) {
                count++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 60001);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1199);
        assertEquals(queuesCount.get("dayQueue"), 1199);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return false if
     * # calls made not exhausting in seconds queue limits and
     * # calls made not exhausting in minutes queue limits and
     * # calls made are less than Day queue call limit (159999) and within Day time limit (1 Day)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than Day queue call limit (159999) and less than Day time limit (1 Day)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_DayQueue_CallLimit_And_LessThan_DayQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int countSeconds = 1;
        int countMinutes = 1;
        for (int i = 0; i < WooConstants.DAY_QUEUE_LIMIT - 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * countSeconds) {
                countSeconds++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            if (i > (WooConstants.MINUTES_QUEUE_LIMIT - 1) * countMinutes) {
                countMinutes++;
                currentDateTime.setTime(currentDateTime.getTime() + 60001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1199);
        assertEquals(queuesCount.get("dayQueue"), 159998);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return false if
     * # calls made not exhausting in seconds queue limits and
     * # calls made not exhausting in minutes queue limits and
     * # calls made are less than Day queue call limit (159999) and more than Day time limit (1 Day)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are less than Day queue call limit (159999) and more than Day time limit (1 Day)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_LessThan_DayQueue_CallLimit_And_MoreThan_DayQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int countSeconds = 1;
        int countMinutes = 1;
        for (int i = 0; i < WooConstants.DAY_QUEUE_LIMIT - 2; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * countSeconds) {
                countSeconds++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            if (i > (WooConstants.MINUTES_QUEUE_LIMIT - 1) * countMinutes) {
                countMinutes++;
                currentDateTime.setTime(currentDateTime.getTime() + 60001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 86400001);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1199);
        assertEquals(queuesCount.get("dayQueue"), 159998);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Method call should return true if
     * # calls made not exhausting in seconds queue limits and
     * # calls made not exhausting in minutes queue limits and
     * # calls made are more than Day queue call limit (159999) and within Day time limit (1 Day)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return true for calls made are more than Day queue call limit (159999) and less than Day time limit (1 Day)")
    void shouldBeTrue_CheckIfLimitsExhausted_If_Calls_MoreThan_DayQueue_CallLimit_And_LessThan_DayQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int countSeconds = 1;
        int countMinutes = 1;
        for (int i = 0; i < WooConstants.DAY_QUEUE_LIMIT + 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * countSeconds) {
                countSeconds++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            if (i > (WooConstants.MINUTES_QUEUE_LIMIT - 1) * countMinutes) {
                countMinutes++;
                currentDateTime.setTime(currentDateTime.getTime() + 60001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 0);
        assertEquals(queuesCount.get("minutesQueue"), 0);
        assertEquals(queuesCount.get("dayQueue"), 0);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), true);
    }

    /**
     * Test Method call should return false if
     * # calls made are not exhausting in seconds queue limits and
     * # calls made are not exhausting in minutes queue limits and
     * # calls made are more than Day queue call limit (159999) and more than Day time limit (1 Day)
     */
    @Test
    @DisplayName("checkIfLimitsExhausted method should return false for calls made are more than Day queue call limit (159999) and more than Day time limit (1 Day)")
    void shouldBeFalse_CheckIfLimitsExhausted_If_Calls_MoreThan_DayQueue_CallLimit_And_MoreThan_DayQueue_TimeLimit() {
        Date currentDateTime = new Date();
        ArrayList<Boolean> proxyExhaustStatusList = new ArrayList<>();
        int countSeconds = 1;
        int countMinutes = 1;
        for (int i = 0; i < WooConstants.DAY_QUEUE_LIMIT - 1; i++) {
            if (i > (WooConstants.SECONDS_QUEUE_LIMIT - 1) * countSeconds) {
                countSeconds++;
                currentDateTime.setTime(currentDateTime.getTime() + 1001);
            }
            if (i > (WooConstants.MINUTES_QUEUE_LIMIT - 1) * countMinutes) {
                countMinutes++;
                currentDateTime.setTime(currentDateTime.getTime() + 60001);
            }
            proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        }
        currentDateTime.setTime(currentDateTime.getTime() + 86400001);
        proxyExhaustStatusList.add(proxyPoolService.checkIfLimitsExhausted(currentDateTime.getTime()));
        HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertEquals(queuesCount.get("secondsQueue"), 49);
        assertEquals(queuesCount.get("minutesQueue"), 1199);
        assertEquals(queuesCount.get("dayQueue"), 159999);
        assertEquals(proxyExhaustStatusList.get(proxyExhaustStatusList.size() - 1), false);
    }

    /**
     * Test Should return Null If No Proxy With InUse Or Ready Status
     */
    @Test
    @DisplayName("getProxy method should return Null If No Proxy With InUse Or Ready Status")
    void shouldBeNull_GetAProxy_If_Proxy_Unavailable() {
        when(proxyListRepository.findFirstByStatus(WooConstants.IN_USE)).thenReturn(null);
        when(proxyListRepository.findFirstByStatus(WooConstants.READY)).thenReturn(null);
        assertNull(proxyPoolService.getAProxy());
    }

    /**
     * Test Should return Null If No Proxy With InUse Or Ready Status
     */
    @Test
    @DisplayName("getProxy method should return valid IP If No Proxy With InUse but have proxy with Ready Status")
    void shouldBeValidProxy_GetAProxy_If_Proxy_Available() {
        ProxyList proxyList = new ProxyList(1L, "103.53.76.82:8089", WooConstants.READY, new Date(), new Date());
        when(proxyListRepository.findFirstByStatus(WooConstants.IN_USE)).thenReturn(null);
        when(proxyListRepository.findFirstByStatus(WooConstants.READY)).thenReturn(proxyList);
        String ip = proxyPoolService.getAProxy();
        assertEquals(ip, "103.53.76.82:8089");
    }


}
