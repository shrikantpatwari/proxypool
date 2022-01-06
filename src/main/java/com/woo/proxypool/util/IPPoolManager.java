package com.woo.proxypool.util;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.service.api.ProxyPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;


import java.util.ArrayList;

@Slf4j
public class IPPoolManager {

    private static IPPoolManager ipPoolManager = null;

    private final ApplicationContext applicationContext;
    private final ProxyPoolService proxyPoolService;

    private IPPoolManager(ApplicationContext context) {
        this.applicationContext = context;
        this.proxyPoolService = context.getBean(ProxyPoolService.class);
    }

    public static IPPoolManager getInstance(ApplicationContext context) {
        if (ipPoolManager == null) {
            ipPoolManager = new IPPoolManager(context);
        }
        return ipPoolManager;
    }

    public void getAndCreateIPPool() {
        log.info("getAndCreateIPPool");
        if (proxyPoolService != null && (null == proxyPoolService.getCountOfDBAvailableIP() || proxyPoolService.getCountOfDBAvailableIP() < 1)) {
            ArrayList<String> proxyList = proxyPoolService.getThirdPartyProxyList();
            // Adding available proxy list in db
            log.info("third party proxy list size = " + proxyList.size());
            ArrayList<ProxyList> proxyListWithStatus = new ArrayList<>();
            proxyList.forEach((proxy) -> {
                if (proxy != null) {
                    proxyListWithStatus.add(new ProxyList(proxy, WooConstants.READY));
                }
            });
            proxyPoolService.addProxyListBulk(proxyListWithStatus);
        }
        /** TODO: Add background running service which check
         * heart beat for each proxy, send kafka event for ip which is dead (with no heart beat response) and update proxy status
         * read kafka event for ip error and update proxy status
         * */
        //
    }

    public ApplicationContext getContext() {
        return applicationContext;
    }
}
