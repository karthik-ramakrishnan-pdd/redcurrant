package com.pdd.redcurrant.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
public class SolaceConfig {

    @Bean
    public DefaultJmsListenerContainerFactory topicListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true); // Enable pub-sub mode for this factory
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory queueListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false); // Queue mode (default)
        return factory;
    }

}
