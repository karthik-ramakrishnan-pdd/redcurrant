package com.digit9.reports.application.controller;

import com.digit9.commons.core.spi.AuthService;
import com.digit9.reports.application.config.BaseUnitTest;
import com.digit9.reports.application.provider.BasicDataProvider;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class TSPReportsControllerTest extends BaseUnitTest {

    @InjectMocks
    private TSPReportsController tspReportsController;

    @Mock
    private TSPReportsServicePort tspReportsService;

    @Mock
    private AuthService authService;

    @Test
    @DisplayName("Get tsp report - success")
    public void getTSPReportSuccess() {
        Mockito.when(authService.getServiceProviderId()).thenReturn(BasicDataProvider.TSP_ID);

        Mockito.when(tspReportsService.findBy(BasicDataProvider.REPORT_ID, BasicDataProvider.TSP_ID))
            .thenReturn(BasicDataProvider.REPORT);

        var response = tspReportsController.get(BasicDataProvider.REPORT_ID);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(BasicDataProvider.REPORT, response);
        Mockito.verify(tspReportsService, Mockito.times(1))
            .findBy(BasicDataProvider.REPORT_ID, BasicDataProvider.TSP_ID);
    }

}
