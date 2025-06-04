package com.pdd.redcurrant.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GCashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GCashResponseCapture;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.ports.spi.StoredProcedurePort;
import com.pdd.redcurrant.domain.registry.ServiceRegistry;
import com.pdd.redcurrant.domain.service.GCashServiceImpl;
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
    public GCashServicePort gCashService(GcashBalanceApi gcashBalanceApi, GcashRemitApi gcashRemitApi,
            ObjectMapper objectMapper, GCashPropertiesConfig gcashPropertiesConfig,
            GCashResponseCapture responseCapture) {
        return new GCashServiceImpl(gcashBalanceApi, gcashRemitApi, objectMapper, gcashPropertiesConfig,
                responseCapture);
    }

}
