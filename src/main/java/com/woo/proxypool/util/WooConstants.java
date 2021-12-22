package com.woo.proxypool.util;

public class WooConstants {
    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    public static final Integer SLEEPING = 0;
    public static final Integer READY = 1;
    public static final Integer IN_USE = 2;
    public static final Integer DEAD = 3;
    public static final Integer EXHAUSTED = 4;

    public static final Long SECONDS_QUEUE_DIFFERENCE_IN_MILLISECONDS = 1000L;
    public static final Integer SECONDS_QUEUE_LIMIT = 49;

    public static final Long MINUTES_QUEUE_DIFFERENCE_IN_MILLISECONDS = 60000L;
    public static final Integer MINUTES_QUEUE_LIMIT = 1199;

    public static final Long DAY_QUEUE_DIFFERENCE_IN_MILLISECONDS = 86400000L;
    public static final Integer DAY_QUEUE_LIMIT = 159999;

}
