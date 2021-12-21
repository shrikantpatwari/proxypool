package com.woo.proxypool.util;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.service.api.ProxyPoolService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class IPPoolManager {
    private static IPPoolManager ipPoolManager = null;

    @Autowired
    ProxyPoolService proxyPoolService;

    private IPPoolManager() {}

    public static IPPoolManager getInstance() {
        if (ipPoolManager == null) {
            ipPoolManager = new IPPoolManager();
        }
        return ipPoolManager;
    }

    public void getAndCreateIPPool() {
        if (proxyPoolService.getCountOfDBAvailableIP() < 1) {
            ArrayList<String> proxyList = proxyPoolService.getThirdPartyProxyList();
            // Adding available proxy list in db
            ArrayList<ProxyList> proxyListWithStatus = new ArrayList<>();
            proxyList.forEach((proxy) -> {
                ProxyList pl = new ProxyList();
                pl.setIp(proxy);
                pl.setStatus(WooConstants.READY);
                proxyListWithStatus.add(pl);
            });
            proxyPoolService.addProxyListBulk(proxyListWithStatus);
        }
        /** TODO: Add background running service which check
         * heart beat for each proxy, send kafka event for ip which is dead (with no heart beat response) and update proxy status
         * read kafka event for ip error and update proxy status
         * */
        //
    }

}
