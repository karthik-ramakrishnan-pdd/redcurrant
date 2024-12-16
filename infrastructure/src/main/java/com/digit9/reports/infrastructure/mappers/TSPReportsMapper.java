package com.digit9.reports.infrastructure.mappers;

import com.digit9.commons.core.utils.IdUtils;
import com.digit9.commons.infra.annotations.IgnoreBaseAudit;
import com.digit9.commons.infra.mappers.D9CommonMapper;
import com.digit9.reports.domain.data.TSPReportsDto;
import com.digit9.reports.infrastructure.entity.TSPReports;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = D9CommonMapper.class, imports = { IdUtils.class })
public interface TSPReportsMapper {

    @IgnoreBaseAudit
    @Mapping(target = "id", expression = "java( IdUtils.generate() )")
    TSPReports map(TSPReportsDto report);

    TSPReportsDto map(TSPReports report);

}
