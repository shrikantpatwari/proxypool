package com.woo.proxypool.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class RateLimitingQueue {
    private static RateLimitingQueue rateLimitingQueue = null;

    private volatile ArrayList<Long> secondsQueue = null;
    private volatile ArrayList<Long> minutesQueue = null;
    private volatile ArrayList<Long> dayQueue = null;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private RateLimitingQueue() {}

    public static RateLimitingQueue getInstance() {
        if (rateLimitingQueue == null) {
            rateLimitingQueue = new RateLimitingQueue();
        }
        return rateLimitingQueue;
    }

    public void initQueues() {
        writeLock.lock();
        try {
            log.info("initialized all queues");
            secondsQueue = new ArrayList<Long>();
            minutesQueue = new ArrayList<Long>();
            dayQueue = new ArrayList<Long>();
        } finally {
            writeLock.unlock();
        }
    }

    public HashMap<String, ArrayList<Long>> getAllQueues() {
        return new HashMap<>() {{
            put("secondsQueue", secondsQueue);
            put("minutesQueue", minutesQueue);
            put("dayQueue", dayQueue);
        }};
    }

    public Long getSecondsQueueFirstElement() {
        readLock.lock();
        Long firstElm = null;
        try {
            firstElm = secondsQueue.get(0);
        } finally {
            readLock.unlock();
        }
        return firstElm;
    }

    public void removeSecondsQueueFirstElement() {
        writeLock.lock();
        try {
            secondsQueue.remove(0);
        } finally {
            writeLock.unlock();
        }
    }

    public Long getMinutesQueueFirstElement() {
        readLock.lock();
        Long firstElm = null;
        try {
            firstElm = minutesQueue.get(0);
        } finally {
            readLock.unlock();
        }
        return firstElm;
    }

    public void removeMinutesQueueFirstElement() {
        writeLock.lock();
        try {
            minutesQueue.remove(0);
        } finally {
            writeLock.unlock();
        }
    }

    public Long getDayQueueFirstElement() {
        readLock.lock();
        Long firstElm = null;
        try {
            firstElm = dayQueue.get(0);
        } finally {
            readLock.unlock();
        }
        return firstElm;
    }

    public void removeDayQueueFirstElement() {
        writeLock.lock();
        try {
            dayQueue.remove(0);
        } finally {
            writeLock.unlock();
        }
    }

    public HashMap<String, Integer> getCountOfItemsInAllQueues() {
        readLock.lock();
        HashMap<String, Integer> queuesCount = null;
        try {
            queuesCount = new HashMap<>() {{
                put("secondsQueue", secondsQueue.size());
                put("minutesQueue", minutesQueue.size());
                put("dayQueue", dayQueue.size());
            }};
        } finally {
            readLock.unlock();
        }
        return queuesCount;
    }

    public boolean addTimeStampToQueues(Long timestamp) {
        writeLock.lock();
        try {
            secondsQueue.add(timestamp);
            minutesQueue.add(timestamp);
            dayQueue.add(timestamp);
        } finally {
            writeLock.unlock();
        }
        return true;
    }

    public Boolean isTimeDifferenceGreaterThanEqualTo(Long timeT1, Long timeT2, Long diffInMilliSeconds) {
        long diff = timeT2 - timeT1;
        return diff >= diffInMilliSeconds;
    }
}
