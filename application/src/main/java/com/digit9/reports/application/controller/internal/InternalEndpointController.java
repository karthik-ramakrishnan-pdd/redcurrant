package com.digit9.reports.application.controller.internal;

import com.digit9.commons.core.annotation.ExcludeFromJacocoGeneratedReport;
import com.digit9.reports.domain.data.TSPReportsDto;
import com.digit9.reports.domain.ports.api.TSPReportsServicePort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1/internal")
@Tag(name = "Internal Endpoints")
@ExcludeFromJacocoGeneratedReport
public class InternalEndpointController {

    private final TSPReportsServicePort tspReportsService;

    @GetMapping(path = "{id}")
    public TSPReportsDto getReport(@PathVariable Long id) {
        return tspReportsService.findBy(id, null);
    }

}