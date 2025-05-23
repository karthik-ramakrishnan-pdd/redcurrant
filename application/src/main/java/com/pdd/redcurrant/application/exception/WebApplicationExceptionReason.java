package com.pdd.redcurrant.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebApplicationExceptionReason implements ApplicationExceptionPolicy {

    //@formatter:off

    METHOD_NOT_SUPPORTED("APP-0002", "The request method '%s' is not supported on this resource",
            HttpStatus.METHOD_NOT_ALLOWED),
    MEDIA_TYPE_NOT_SUPPORTED("APP-0003", "%s", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    REQUEST_VALIDATION_ERROR("APP-0004", "Request validation failed", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_READABLE_ERROR("APP-0005", "Request read failed with exception: %s", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("APP-0006", "Authentication failed: %s", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("APP-0007", "Authorization failed: %s", HttpStatus.FORBIDDEN),
    MALFORMED_DOWNSTREAM_RESPONSE("APP-0008", "Downstream response was missing required or expected information", HttpStatus.BAD_GATEWAY),
    INVALID_PARTNER_WALLET_PIN("APP-0009", "Provided wallet PIN is incorrect", HttpStatus.BAD_REQUEST),
    PARTNER_WALLET_NOT_FOUND("APP-0010", "Partner Wallet cannot be found", HttpStatus.BAD_REQUEST),
    GCASH_GENERIC_FAILURE("APP-0011", "Generic Gcash failure", HttpStatus.BAD_REQUEST);

    //@formatter:on

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;

}
