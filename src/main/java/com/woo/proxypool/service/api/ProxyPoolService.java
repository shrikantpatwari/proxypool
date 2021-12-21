package com.woo.proxypool.service.api;

import com.woo.proxypool.data.entity.ProxyList;

import java.util.ArrayList;

public interface ProxyPoolService {
    String getAProxy();

    ArrayList<String> getThirdPartyProxyList();

    Long getCountOfDBAvailableIP();

    void addProxyListBulk(ArrayList<ProxyList> proxies);
}
