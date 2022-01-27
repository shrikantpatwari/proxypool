package com.woo.proxypool.service.impl;

import com.bugsnag.Bugsnag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.data.entity.UserProxyMap;
import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.data.repository.UserProxyRepository;
import com.woo.proxypool.service.api.ProxyPoolService;
import com.woo.proxypool.util.RateLimitingQueue;
import com.woo.proxypool.util.WooConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyPoolServiceImpl implements ProxyPoolService {

    private final ProxyListRepository proxyListRepository;

    private final UserProxyRepository userProxyRepository;

    private final RateLimitingQueue rateLimitingQueue = RateLimitingQueue.getInstance();

    private final ReentrantLock reLock = new ReentrantLock(true);

    private final Bugsnag bugsnag;

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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.smartproxy.com/v1/endpoints-custom?proxyType=residential_proxies&authType=basic&username=sp65815615%2520&password=w00Us3r82%2521&sessionType=sticky&sessionTime=10&location=us&outputFormat=protocol%253Aauth%2540endpoint&count=10&page=1&responseFormat=json&domain=smartproxy.com")
                .get()
                .addHeader("Accept", "application/json")
                .build();
        Response response;
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<ArrayList<String>>(){});
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
        ArrayList<String> proxies = new ArrayList<>();
//        RequestBody requestBody = RequestBody.create("", null);
//        Request tokenRequest = new Request.Builder()
//                .url("https://api.smartproxy.com/v1/auth")
//                .post(requestBody)
//                .addHeader("Accept", "application/json")
//                .addHeader("Authorization", "Basic c3A2NTgxNTYxNSA6dzAwVXMzcjgyIQ==")
//                .build();
//
//        try {
//
//            Response tokenResponse = client.newCall(tokenRequest).execute();
//            if (tokenResponse.isSuccessful()) {
//                JSONObject tokenRespJson = new JSONObject(Objects.requireNonNull(tokenResponse.body()).string());
//                String token = tokenRespJson.getString("token");
//                log.info(token);
//                if (token != null && !token.isEmpty()) {
//                    Request request = new Request.Builder()
//                            .url("https://api.smartproxy.com/v1/users/userId/whitelisted-ips")
//                            .get()
//                            .addHeader("Accept", "application/json")
//                            .addHeader("Authorization", "Token " + token)
//                            .build();
//                    response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        String responseString = Objects.requireNonNull(response.body()).string();
//                        log.info(responseString);
//                        ArrayList<HashMap<String, Object>> result = mapper.readValue(responseString, new TypeReference<ArrayList<HashMap<String, Object>>>(){});
//                        for (HashMap<String, Object> proxy:
//                             result) {
//                            log.info(proxy.get("ip").toString());
//                            proxies.add(proxy.get("ip").toString());
//                        }
//                    } else if (response.code() >= 400 && response.code() <=499) {
//                        log.error(Objects.requireNonNull(response.body()).string());
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            bugsnag.notify(e);
//            log.error(e.getMessage(), e);
//        } catch (NullPointerException npe) {
//            bugsnag.notify(npe);
//            log.error(npe.getMessage(), npe);
//        }
        log.info("" + proxies.size());
        if (proxies.size() < 1) {
            proxies.addAll(new ArrayList<>() {{
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
            }});
        }
        return proxies;
    }

    @Override
    public Long getCountOfDBAvailableIP() {
        Long count = proxyListRepository.count();
        log.info(String.valueOf(count));
        return count;
    }

    @Override
    public void addProxyListBulk(ArrayList<ProxyList> proxies) {
        proxyListRepository.saveAll(proxies);
    }

    @Override
    public ProxyList getReadyOrInUserIPFromDB() {
        ProxyList proxy = null;
        try {
            proxy = proxyListRepository.findFirstByStatus(WooConstants.IN_USE);
            if (null == proxy) {
                proxy = proxyListRepository.findFirstByStatus(WooConstants.READY);
                if (proxy != null) {
                    RateLimitingQueue.getInstance().initQueues();
                }
            }
        } catch (Exception e) {
            // TODO: Set proper error message for exception
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
        if (proxy != null) {
            log.info(proxy.getIp());
            proxy.setStatus(WooConstants.IN_USE);
            proxyListRepository.save(proxy);
            return proxy;
        }
        return null;
    }

    @Override
    public Boolean checkIfLimitsExhausted(Long time) {
        reLock.lock();
        try {
            boolean limitExhausted = false;
            HashMap<String, Integer> queuesCount = rateLimitingQueue.getCountOfItemsInAllQueues();
            if (queuesCount.get("secondsQueue") >= WooConstants.SECONDS_QUEUE_LIMIT ) {
                if (rateLimitingQueue.isTimeDifferenceGreaterThanEqualTo(rateLimitingQueue.getSecondsQueueFirstElement(), time, WooConstants.SECONDS_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    rateLimitingQueue.removeSecondsQueueFirstElement();
                } else {
                    rateLimitingQueue.initQueues();
                    limitExhausted = true;
                }
            }
            if (!limitExhausted && queuesCount.get("minutesQueue") >= WooConstants.MINUTES_QUEUE_LIMIT) {
                if (rateLimitingQueue.isTimeDifferenceGreaterThanEqualTo(rateLimitingQueue.getMinutesQueueFirstElement(), time, WooConstants.MINUTES_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    rateLimitingQueue.removeMinutesQueueFirstElement();
                } else {
                    rateLimitingQueue.initQueues();
                    limitExhausted = true;
                }
            }
            if (!limitExhausted && queuesCount.get("dayQueue") >= WooConstants.DAY_QUEUE_LIMIT) {
                if (rateLimitingQueue.isTimeDifferenceGreaterThanEqualTo(rateLimitingQueue.getDayQueueFirstElement(), time, WooConstants.DAY_QUEUE_DIFFERENCE_IN_MILLISECONDS)) {
                    rateLimitingQueue.removeDayQueueFirstElement();
                } else {
                    rateLimitingQueue.initQueues();
                    limitExhausted = true;
                }
            }
            rateLimitingQueue.addTimeStampToQueues(time);
            return limitExhausted;
        } catch (Exception e) {
            // TODO: Set proper error message for exception
            log.error(e.getMessage(), e);
        } finally {
            reLock.unlock();
        }
        return true;
    }

    @Override
    public ProxyList getReadyProxyFromDB() {
        ProxyList proxy = null;
        try {
            proxy = proxyListRepository.findFirstByStatus(WooConstants.READY);
        } catch (Exception e) {
            // TODO: Set proper error message for exception
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
        if (proxy != null) {
            if (proxy.getUsageCount() < WooConstants.MAX_USAGE_LIMIT) {
                proxy.setUsageCount(proxy.getUsageCount() + 1);
            } else {
                proxy.setStatus(WooConstants.EXHAUSTED);
            }
            proxyListRepository.save(proxy);
            return proxy;
        }
        return null;
    }

    @Override
    public void assignProxyToUser(JSONObject activatedUser) {
        try {
            UserProxyMap oldEntry = userProxyRepository.findFirstByUserId(activatedUser.getString("ClientId"));
            if (oldEntry == null) {
                ProxyList proxy = this.getReadyProxyFromDB();
                userProxyRepository.save(new UserProxyMap(proxy.getIp(), activatedUser.getString("ClientId")));
            } else {
                log.error("proxy already assigned", oldEntry.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
    }

    @Override
    public void assignNewProxy(JSONObject user) {
        try {
            ProxyList usedProxy = proxyListRepository.findFirstByIp(user.getString("proxy"));
            if (usedProxy != null) {
                usedProxy.setStatus(WooConstants.EXHAUSTED);
                proxyListRepository.save(usedProxy);
            } else {
                log.error("no proxy found with given ip");
            }
            UserProxyMap existingRecord = userProxyRepository.findFirstByUserId(user.getString("ClientId"));
            if (existingRecord != null) {
                ProxyList newProxy = this.getReadyProxyFromDB();
                if (newProxy != null) {
                    existingRecord.setProxy(newProxy.getIp());
                    userProxyRepository.save(existingRecord);
                } else {
                    log.error("no new proxy found with ready status");
                }
            } else {
                this.assignProxyToUser(user);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
    }


    @Override
    public String getProxyForUser(String userId) {
        try {
            UserProxyMap userProxyMap = userProxyRepository.findFirstByUserId(userId);
            if (userProxyMap != null) {
                return userProxyMap.getProxy();
            } else {
                JSONObject clientObj = new JSONObject();
                clientObj.put("ClientId", userId);
                this.assignProxyToUser(clientObj);
                return userProxyRepository.findFirstByUserId(userId).getProxy();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bugsnag.notify(e);
        }
        return null;
    }
}
