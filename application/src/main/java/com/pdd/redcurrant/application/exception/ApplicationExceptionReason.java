package com.pdd.redcurrant.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionReason implements ApplicationExceptionPolicy {

    //@formatter:off

    REQUEST_VALIDATION_ERROR("APP-001", "Request validation failed", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("APP-002", "Authentication failed: %s", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("APP-003", "Authorization failed: %s", HttpStatus.FORBIDDEN),

    ;
    //@formatter:on

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;

}
