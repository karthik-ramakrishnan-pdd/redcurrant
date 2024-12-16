package com.digit9.reports.domain.exceptions;

import com.digit9.commons.core.data.exceptions.HttpStatus;
import com.digit9.commons.core.data.exceptions.policy.BusinessExceptionPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the Merchant Beneficiary business exception reasons.
 */
@Getter
@AllArgsConstructor
public enum ReportsBusinessExceptionReason implements BusinessExceptionPolicy {

    //@formatter:off

    TSP_REPORT_NOT_FOUND("RBE-0001", "TSP Report not found", HttpStatus.NOT_FOUND)
    ;

    //@formatter:on

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;

}
