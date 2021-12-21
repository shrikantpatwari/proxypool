package com.woo.proxypool.util;

import java.util.ArrayList;
import java.util.HashMap;

public class RateLimitingQueue {
    private static RateLimitingQueue rateLimitingQueue = null;

    private ArrayList<Long> secondsQueue = null;
    private ArrayList<Long> minutesQueue = null;
    private ArrayList<Long> dayQueue = null;

    private RateLimitingQueue() {}

    public static RateLimitingQueue getInstance() {
        if (rateLimitingQueue == null) {
            rateLimitingQueue = new RateLimitingQueue();
        }
        return rateLimitingQueue;
    }

    public void initQueues () {
        secondsQueue = new ArrayList<Long>();
        minutesQueue = new ArrayList<Long>();
        dayQueue = new ArrayList<Long>();
    }

    public HashMap<String, ArrayList<Long>> getAllQueues() {
        return new HashMap<>() {{
            put("secondsQueue", secondsQueue);
            put("minutesQueue", minutesQueue);
            put("dayQueue", dayQueue);
        }};
    }

    public HashMap<String, Integer> getCountOfItemsInAllQueues() {
        return new HashMap<>() {{
            put("secondsQueue", secondsQueue.size());
            put("minutesQueue", minutesQueue.size());
            put("dayQueue", dayQueue.size());
        }};
    }
}
