package com.digit9.reports.infrastructure.adapters;

import com.digit9.commons.infra.adaptors.D9SpecHelper;
import com.digit9.reports.domain.data.TSPReportsDto;
import com.digit9.reports.domain.ports.spi.TSPReportsPersistencePort;
import com.digit9.reports.infrastructure.entity.TSPReports;
import com.digit9.reports.infrastructure.entity.TSPReports_;
import com.digit9.reports.infrastructure.mappers.TSPReportsMapper;
import com.digit9.reports.infrastructure.repository.TSPReportsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TSPReportsJpaAdapter extends D9SpecHelper<TSPReports> implements TSPReportsPersistencePort {

    private final TSPReportsRepository repository;

    private final TSPReportsMapper mapper;

    @Override
    public Optional<TSPReportsDto> findBy(Long id, String tspId) {
        Specification<TSPReports> spec = Specification.where(eq(TSPReports_.ID, id)).and(eq(TSPReports_.TSP_ID, tspId));
        return repository.findOne(spec).map(mapper::map);
    }

}
