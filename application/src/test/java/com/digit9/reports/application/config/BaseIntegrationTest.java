package com.digit9.reports.application.config;

import com.digit9.commons.core.spi.AuthService;
import com.digit9.commons.core.spi.IdmClientPort;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

public abstract class BaseIntegrationTest extends TestConfiguration {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected IdmClientPort idmClient;

    @MockBean
    protected TSPReportsServicePort tspReportsService;

}
