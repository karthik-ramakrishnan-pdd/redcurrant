package com.digit9.reports.configuration;

import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import com.digit9.reports.domain.ports.spi.TSPReportsPersistencePort;
import com.digit9.reports.domain.service.TSPReportsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanConfig {

    @Bean
    public TSPReportsServicePort tspReportsService(TSPReportsPersistencePort tspReportsJpaAdapter) {
        return new TSPReportsServiceImpl(tspReportsJpaAdapter);
    }

}
