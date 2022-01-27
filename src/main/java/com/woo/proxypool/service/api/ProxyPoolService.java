package com.woo.proxypool.service.api;

import com.woo.proxypool.data.entity.ProxyList;
import org.json.JSONObject;

import java.util.ArrayList;

public interface ProxyPoolService {
    String getAProxy();

    ArrayList<String> getThirdPartyProxyList();

    Long getCountOfDBAvailableIP();

    void addProxyListBulk(ArrayList<ProxyList> proxies);

    ProxyList getReadyOrInUserIPFromDB();

    Boolean checkIfLimitsExhausted(Long time);

    ProxyList getReadyProxyFromDB();

    void assignProxyToUser(JSONObject activatedUser);

    void assignNewProxy(JSONObject user);

    String getProxyForUser(String userId);
}
