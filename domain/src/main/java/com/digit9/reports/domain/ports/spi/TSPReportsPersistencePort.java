package com.digit9.reports.domain.ports.spi;

import com.digit9.reports.domain.data.TSPReportsDto;

import java.util.Optional;

public interface TSPReportsPersistencePort {

    Optional<TSPReportsDto> findBy(Long id, String tspId);

}
