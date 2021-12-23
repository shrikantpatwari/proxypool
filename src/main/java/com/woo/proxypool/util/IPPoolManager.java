package com.woo.proxypool.util;

import com.woo.proxypool.data.entity.ProxyList;
import com.woo.proxypool.service.api.ProxyPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


import java.util.ArrayList;

public class IPPoolManager {
    Logger logger = LoggerFactory.getLogger(IPPoolManager.class);
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
        logger.info("getAndCreateIPPool");
        if (proxyPoolService != null && (null == proxyPoolService.getCountOfDBAvailableIP() || proxyPoolService.getCountOfDBAvailableIP() < 1)) {
            ArrayList<String> proxyList = proxyPoolService.getThirdPartyProxyList();
            // Adding available proxy list in db
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
