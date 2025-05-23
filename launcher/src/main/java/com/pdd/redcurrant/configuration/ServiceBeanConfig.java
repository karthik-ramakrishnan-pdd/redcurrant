package com.pdd.redcurrant.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
import com.pdd.redcurrant.domain.ports.api.GcashServicePort;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import com.pdd.redcurrant.domain.registry.ServiceRegistry;
import com.pdd.redcurrant.domain.service.GcashServiceImpl;
import com.pdd.redcurrant.domain.service.SolaceServiceImpl;
import com.pdd.redcurrant.domain.service.StoredProcedureServiceImpl;
import com.redcurrant.downstream.api.gcash.GcashBalanceApi;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
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
    public GcashServicePort gCashService(GcashBalanceApi gcashBalanceApi, GcashRemitApi gcashRemitApi,
            ObjectMapper objectMapper, GcashPropertiesConfig gcashPropertiesConfig) {
        return new GcashServiceImpl(gcashBalanceApi, gcashRemitApi, objectMapper, gcashPropertiesConfig);
    }

}
