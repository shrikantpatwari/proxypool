package com.woo.proxypool.service.impl;

import com.woo.proxypool.service.api.ProxyPoolService;
import com.woo.proxypool.util.WooConstants;
import org.springframework.stereotype.Service;

@Service
public class ProxyPoolServiceImpl implements ProxyPoolService {

    @Override
    public String getProxy(String type) {
        if (type.equalsIgnoreCase(WooConstants.HTTP)) {

        } else if (type.equalsIgnoreCase(WooConstants.HTTPS)) {

        }
        return null;
    }
}
