package com.woo.proxypool.controller;


import com.woo.proxypool.data.repository.ProxyListRepository;
import com.woo.proxypool.service.api.ProxyPoolService;
import com.woo.proxypool.util.RateLimitingQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProxyPoolControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProxyPoolService proxyPoolService;

    @Test
    void getProxyIntegrationTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/proxy/")).andExpect(status().is(200)).andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();
        assertEquals(proxyPoolService.getReadyOrInUserIPFromDB().getIp(), mvcResult.getResponse().getContentAsString());
        assertSame(proxyPoolService.getCountOfDBAvailableIP(), (long) proxyPoolService.getThirdPartyProxyList().size());
        assertSame(RateLimitingQueue.getInstance().getCountOfItemsInAllQueues().get("secondsQueue"), 1);
    }
}
