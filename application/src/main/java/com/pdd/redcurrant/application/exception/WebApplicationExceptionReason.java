package com.pdd.redcurrant.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebApplicationExceptionReason implements ApplicationExceptionPolicy {

    METHOD_NOT_SUPPORTED("APP-0002", "The request method '%s' is not supported on this resource",
            HttpStatus.METHOD_NOT_ALLOWED),
    MEDIA_TYPE_NOT_SUPPORTED("APP-0003", "%s", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    REQUEST_VALIDATION_ERROR("APP-0004", "Request validation failed", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_READABLE_ERROR("APP-0005", "Request read failed with exception: %s", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("APP-0006", "Authentication failed: %s", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("APP-0007", "Authorization failed: %s", HttpStatus.FORBIDDEN);

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;

}
