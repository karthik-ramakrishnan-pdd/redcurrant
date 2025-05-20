package com.pdd.redcurrant.configuration;

import com.pdd.redcurrant.domain.mappers.GCashMapper;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import com.pdd.redcurrant.domain.registry.ServiceRegistry;
import com.pdd.redcurrant.domain.service.GCashServiceImpl;
import com.pdd.redcurrant.domain.service.SolaceServiceImpl;
import com.pdd.redcurrant.domain.service.StoredProcedureServiceImpl;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanConfig {

    @Bean
    public StoredProcedureServicePort storedProcedureService(StoredProcedurePort storedProcedureJdbcAdapter) {
        return new StoredProcedureServiceImpl(storedProcedureJdbcAdapter);
    }

    @Bean
    public SolaceServicePort solaceService(ServiceRegistry serviceRegistry, Validator validator) {
        return new SolaceServiceImpl(serviceRegistry, validator);
    }

    @Bean
    public GCashServicePort gCashService(GCashMapper gCashMapper) {
        return new GCashServiceImpl(gCashMapper);
    }

}
