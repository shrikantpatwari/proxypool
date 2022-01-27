package com.woo.proxypool.config;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    @Autowired
    Environment env;
    @Bean
    public Bugsnag bugsnag() {
        return new Bugsnag(env.getProperty("bugsnag.apikey"));
    }
}
