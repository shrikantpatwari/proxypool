package com.woo.proxypool.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateLimitingQueueTests {

    private final RateLimitingQueue rateLimitingQueue = RateLimitingQueue.getInstance();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkRateLimitingQueue_Are_Non_Initiated() {
        HashMap<String, ArrayList<Long>> rateLimitingQueues = rateLimitingQueue.getAllQueues();
        assertNull(rateLimitingQueues.get("secondsQueue"));
        assertNull(rateLimitingQueues.get("minutesQueue"));
        assertNull(rateLimitingQueues.get("dayQueue"));
    }

    @Test
    void checkRateLimitingQueue_Queue_Initiation() {
        rateLimitingQueue.initQueues();
        HashMap<String, ArrayList<Long>> rateLimitingQueues = rateLimitingQueue.getAllQueues();
        Assertions.assertNotNull(rateLimitingQueues.get("secondsQueue"));
        Assertions.assertNotNull(rateLimitingQueues.get("minutesQueue"));
        Assertions.assertNotNull(rateLimitingQueues.get("dayQueue"));
    }

    @Test
    void getCountOfItemsInAllQueues() {
        rateLimitingQueue.initQueues();
        HashMap<String, Integer> map = rateLimitingQueue.getCountOfItemsInAllQueues();
        Assertions.assertNotNull(map);
    }

    @Test
    void getAllQueues() {
        rateLimitingQueue.initQueues();
        HashMap<String, ArrayList<Long>> map = rateLimitingQueue.getAllQueues();
        Assertions.assertNotNull(map);
    }

    @Test
    void isTimeDifferenceGreaterThanEqualTo() {
        rateLimitingQueue.initQueues();
        Boolean result = rateLimitingQueue.isTimeDifferenceGreaterThanEqualTo(50l, 20l, 1l);
        assertEquals(result, false);

        Boolean result1 = rateLimitingQueue.isTimeDifferenceGreaterThanEqualTo(50l, 70l, 10l);
        assertEquals(result1, true);
    }

}
