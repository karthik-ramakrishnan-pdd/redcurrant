package com.digit9.reports.domain.ports.api;

import com.digit9.reports.domain.data.TSPReportsDto;

public interface TSPReportsServicePort {

    TSPReportsDto findBy(Long id, String tspId);

}
