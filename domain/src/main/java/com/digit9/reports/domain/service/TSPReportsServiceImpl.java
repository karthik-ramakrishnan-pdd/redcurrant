package com.digit9.reports.domain.service;

import com.digit9.commons.core.data.exceptions.BusinessException;
import com.digit9.reports.domain.data.TSPReportsDto;
import com.digit9.reports.domain.exceptions.ReportsBusinessExceptionReason;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import com.digit9.reports.domain.ports.spi.TSPReportsPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TSPReportsServiceImpl implements TSPReportsServicePort {

    private final TSPReportsPersistencePort tspReportsJpaAdapter;

    @Override
    public TSPReportsDto findBy(Long id, String tspId) {
        return tspReportsJpaAdapter.findBy(id, tspId)
            .orElseThrow(() -> new BusinessException(ReportsBusinessExceptionReason.TSP_REPORT_NOT_FOUND));
    }

}
