package com.digit9.reports.application.controller;

import com.digit9.commons.core.spi.AuthService;
import com.digit9.commons.web.annotations.Digit9Controller;
import com.digit9.commons.web.annotations.Digit9GetMapping;
import com.digit9.reports.application.constants.ControllerConstant;
import com.digit9.reports.domain.data.TSPReportsDto;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Digit9Controller(path = "s/api/v1/reports", name = ControllerConstant.TSP_REPORTS,
        description = "TSP Reports operations")
public class TSPReportsController {

    private final AuthService authService;

    private final TSPReportsServicePort reportsService;

    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    @Digit9GetMapping(path = "/{id}", summary = "Get report", tags = ControllerConstant.TSP_REPORTS)
    public TSPReportsDto get(@PathVariable Long id) {
        return reportsService.findBy(id, authService.getServiceProviderId());
    }

}