package com.woo.proxypool.service.impl;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.api.ProxyPoolService;
import com.woo.proxypool.util.RateLimitingQueue;
import com.woo.proxypool.util.WooConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Service
public class ProxyPoolServiceImpl implements ProxyPoolService {

    Logger logger = LoggerFactory.getLogger(ProxyPoolServiceImpl.class);

    @Autowired
    ProxyListRepository proxyListRepository;

    @Override
    public String getAProxy() {
        ProxyList inUseProxy = this.getReadyOrInUserIPFromDB();
        if (inUseProxy != null) {
            Date currentDate = new Date();
            if (this.checkIfLimitsExhausted(currentDate.getTime())) {
                inUseProxy.setStatus(WooConstants.EXHAUSTED);
                proxyListRepository.save(inUseProxy);
                ProxyList newIp = this.getReadyOrInUserIPFromDB();
                if (newIp != null) {
                    return newIp.getIp();
                }
            }
            return inUseProxy.getIp();
        }
        return null;
    }

    @Override
    public ArrayList<String> getThirdPartyProxyList() {
        return new ArrayList<>() {{
            add("103.53.76.82:8089");
            add("103.93.100.130:8080");
            add("108.61.186.207:8080");
            add("110.37.216.118:8080");
            add("110.44.133.135:3128");
            add("117.222.58.170:8080");
            add("118.163.83.21:3128");
            add("118.99.67.242:8080");
            add("124.158.179.12:8080");
            add("124.41.211.153:39433");
            add("128.199.230.42:3128");
            add("139.178.81.19:80");
            add("140.238.15.222:3128");
            add("149.129.77.78:3128");
            add("159.224.83.100:8080");
            add("159.89.128.93:3128");
            add("168.138.42.43:3128");
            add("178.128.223.4:3128");
            add("178.128.24.20:8080");
            add("181.143.222.228:8080");
            add("181.177.140.123:8080");
            add("182.48.82.181:8080");
            add("183.88.242.150:8080");
            add("183.89.95.238:8080");
            add("185.122.57.238:3128");
            add("185.56.209.114:51386");
            add("186.0.176.147:443");
            add("186.24.6.194:8181");
            add("187.16.4.126:8080");
            add("187.87.76.251:3128");
            add("191.242.230.135:8080");
            add("195.191.182.76:8080");
            add("197.232.36.43:8080");
            add("200.69.74.170:8080");
            add("206.189.36.13:47503");
            add("206.81.10.180:3128");
            add("213.234.12.114:8080");
            add("216.198.188.26:51068");
            add("27.145.61.233:8080");
            add("36.91.44.243:37927");
            add("36.92.93.107:8080");
            add("37.120.161.249:3128");
            add("38.21.34.224:8080");
            add("45.127.134.195:8080");
            add("45.160.168.254:8080");
            add("45.177.16.132:999");
            add("45.236.170.9:3282");
            add("5.189.134.87:3128");
            add("51.38.71.101:8080");
            add("51.68.61.17:8080");
            add("61.163.32.88:3128");
            add("62.171.161.146:8080");
            add("69.65.65.178:34548");
            add("82.114.120.18:8080");
            add("88.149.203.30:46909");
            add("92.119.61.18:9090");
            add("92.33.17.248:8080");
        }};
    }

    @Override
    public Long getCountOfDBAvailableIP() {
        Long count = proxyListRepository.count();
        logger.info(String.valueOf(count));
        return count;
    }

    @Override
    public void addProxyListBulk(ArrayList<ProxyList> proxies) {
        proxies.forEach((p) -> {
            logger.info( "addProxyListBulk" + p.toString());
        });
        proxyListRepository.saveAll(proxies);
    }

    @Override
    public ProxyList getReadyOrInUserIPFromDB() {
        ProxyList proxy = null;
        try {
            proxy = proxyListRepository.findOneByStatus(WooConstants.IN_USE);
            if (null == proxy) {
                proxy = proxyListRepository.findOneByStatus(WooConstants.READY);
                if (proxy != null) {
                    RateLimitingQueue.getInstance().initQueues();
                }
            }
        } catch (Exception e) {
            // TODO: Set proper error message for exception
        }
        if (proxy != null) {
            proxy.setStatus(WooConstants.IN_USE);
            proxyListRepository.save(proxy);
            return proxy;
        }
        return null;
    }

    @Override
    public Boolean checkIfLimitsExhausted(Long time) {
        try {
            Boolean limitExhausted = false;
            HashMap<String, Integer> queuesCount = RateLimitingQueue.getInstance().getCountOfItemsInAllQueues();
            HashMap<String, ArrayList<Long>> queues = RateLimitingQueue.getInstance().getAllQueues();
            ArrayList<Long> secondsQueue = queues.get("secondsQueue");
            ArrayList<Long> minutesQueue = queues.get("minutesQueue");
            ArrayList<Long> dayQueue = queues.get("dayQueue");
            if (queuesCount.get("secondsQueue") > WooConstants.SECONDS_QUEUE_LIMIT ) {
                if (RateLimitingQueue.getInstance().isTimeDifferenceGreaterThanEqualTo(secondsQueue.get(0), time, WooConstants.SECONDS_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    RateLimitingQueue.getInstance().initQueues();
                    limitExhausted = true;
                } else {
                    secondsQueue.remove(0);
                }
            }
            if (!limitExhausted && queuesCount.get("minutesQueue") > WooConstants.MINUTES_QUEUE_LIMIT) {
                if (RateLimitingQueue.getInstance().isTimeDifferenceGreaterThanEqualTo(minutesQueue.get(0), time, WooConstants.MINUTES_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    RateLimitingQueue.getInstance().initQueues();
                    limitExhausted = true;
                } else {
                    minutesQueue.remove(0);
                }
            }
            if (!limitExhausted && queuesCount.get("dayQueue") > WooConstants.DAY_QUEUE_LIMIT) {
                if (RateLimitingQueue.getInstance().isTimeDifferenceGreaterThanEqualTo(minutesQueue.get(0), time, WooConstants.DAY_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    RateLimitingQueue.getInstance().initQueues();
                    limitExhausted = true;
                } else {
                    dayQueue.remove(0);
                }
            }
            secondsQueue.add(time);
            minutesQueue.add(time);
            dayQueue.add(time);
            return limitExhausted;
        } catch (Exception e) {
            // TODO: Set proper error message for exception
        }
        return true;
    }
}
