package com.woo.proxypool.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.woo.proxypool.service.api.ProxyPoolService;

@WebMvcTest(controllers = ProxyPoolController.class)
public class ProxyPoolControllerTest {

    @MockBean
    private ProxyPoolService proxyPoolService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should List All Posts When making GET request to endpoint - /proxy")
    public void shouldCreatePost() throws Exception {
        Mockito.when(proxyPoolService.getAProxy()).thenReturn("103.53.76.82:8089");

        mockMvc.perform(get("/proxy/")).andExpect(status().is(200)).andExpect(content().string("103.53.76.82:8089"));
    }
}
