package com.digit9.reports.domain.service;

import com.digit9.reports.domain.config.BaseUnitTest;
import com.digit9.reports.domain.ports.spi.TSPReportsPersistencePort;
import com.digit9.reports.domain.provider.BasicDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class TSPReportsServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private TSPReportsServiceImpl tspReportsService;

    @Mock
    private TSPReportsPersistencePort tspReportsPersistence;

    @Test
    @DisplayName("Get TSP Report method - success")
    public void getTSPReport() {
        Mockito.when(tspReportsPersistence.findBy(1L, BasicDataProvider.TSP_ID))
            .thenReturn(Optional.ofNullable(BasicDataProvider.REPORT));

        var response = tspReportsService.findBy(BasicDataProvider.REPORT_ID, BasicDataProvider.TSP_ID);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(BasicDataProvider.REPORT, response);
        Mockito.verify(tspReportsPersistence, Mockito.times(1))
            .findBy(BasicDataProvider.REPORT_ID, BasicDataProvider.TSP_ID);
    }

}
