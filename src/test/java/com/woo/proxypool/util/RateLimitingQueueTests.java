package com.woo.proxypool.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateLimitingQueueTests {

    private RateLimitingQueue rateLimitingQueue = RateLimitingQueue.getInstance();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCountOfItemsInAllQueues() {
        rateLimitingQueue.initQueues();
        HashMap<String, Integer> map = rateLimitingQueue.getCountOfItemsInAllQueues();
        assertNotNull(map);
    }

    @Test
    void getAllQueues() {
        rateLimitingQueue.initQueues();
        HashMap<String, ArrayList<Long>> map = rateLimitingQueue.getAllQueues();
        assertNotNull(map);
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
