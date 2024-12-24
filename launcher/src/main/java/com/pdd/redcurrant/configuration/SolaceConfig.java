package com.pdd.redcurrant.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceConfig {

    // Add Solace PubSub+ Configuration Here
    @Bean
    public String solaceConfiguration() {
        return "Solace PubSub+ configured";
    }
}
