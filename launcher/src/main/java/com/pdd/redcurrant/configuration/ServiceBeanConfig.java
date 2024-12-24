package com.pdd.redcurrant.configuration;

import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import com.pdd.redcurrant.domain.service.StoredProcedureServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanConfig {

    @Bean
    public StoredProcedureServicePort tspReportsService(StoredProcedurePort tspReportsJpaAdapter) {
        return new StoredProcedureServiceImpl(tspReportsJpaAdapter);
    }

}
