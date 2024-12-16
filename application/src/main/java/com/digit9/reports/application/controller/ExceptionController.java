package com.digit9.reports.application.controller;

import com.digit9.commons.core.data.exceptions.BusinessException;
import com.digit9.commons.core.data.exceptions.policy.GlobalExceptionReason;
import com.digit9.commons.web.annotations.Digit9Controller;
import com.digit9.commons.web.annotations.Digit9GetMapping;
import com.digit9.reports.application.constants.ControllerConstant;
import com.digit9.reports.domain.data.TSPReportsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RequiredArgsConstructor
@Digit9Controller(path = "s/api/v1/exception", name = ControllerConstant.TSP_EXCEPTION, description = "TSP Exceptions")
public class ExceptionController {

    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    @Digit9GetMapping(summary = "Get exception", tags = ControllerConstant.TSP_EXCEPTION)
    public TSPReportsDto getException() {
        throw new BusinessException(GlobalExceptionReason.UNKNOWN_ERROR);
    }

}