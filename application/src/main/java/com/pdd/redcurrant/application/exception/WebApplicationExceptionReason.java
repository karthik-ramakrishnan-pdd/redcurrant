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
    REQUEST_VALIDATION_ERROR("APP-0004", "Request validation failed with error: %s", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_READABLE_ERROR("APP-0005", "Request read failed with exception: %s", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("APP-0006", "Authentication failed: %s", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("APP-0007", "Authorization failed: %s", HttpStatus.FORBIDDEN),
    MALFORMED_DOWNSTREAM_RESPONSE("APP-0008", "Downstream response was missing required or expected information", HttpStatus.BAD_GATEWAY),
    INVALID_PARTNER_WALLET_PIN("APP-0009", "Provided wallet PIN is incorrect", HttpStatus.BAD_REQUEST),
    PARTNER_WALLET_NOT_FOUND("APP-0010", "Partner Wallet cannot be found", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVE("APP-0011", "GCash account is not active", HttpStatus.BAD_REQUEST),
    TARGET_NOT_KYCED("APP-0012", "Receiver is not KYCed", HttpStatus.BAD_REQUEST),
    WALLET_LIMIT_EXCEEDED("APP-0013", "Wallet limit exceeded", HttpStatus.BAD_REQUEST),
    COUNTRY_BLACKLISTED("APP-0014", "Transfer to this country is blocked", HttpStatus.BAD_REQUEST),
    RISK_REJECTED("APP-0015", "Transaction rejected due to risk check", HttpStatus.BAD_REQUEST),
    COMPLIANCE_FAILURE("APP-0016", "Compliance check failed", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("APP-0017", "Invalid transaction amount", HttpStatus.BAD_REQUEST),
    CREDIT_FAILED("APP-0018", "Credit to wallet failed", HttpStatus.BAD_GATEWAY),
    INSUFFICIENT_BALANCE("APP-0019", "Partner wallet has insufficient balance", HttpStatus.INTERNAL_SERVER_ERROR),
    GCASH_INTERNAL_ERROR("APP-0020", "GCash internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    GCASH_RETRY_LATER("APP-0021", "GCash is processing – try again later", HttpStatus.INTERNAL_SERVER_ERROR),
    GCASH_GENERIC_FAILURE("APP-0099", "Generic Gcash failure: %s", HttpStatus.INTERNAL_SERVER_ERROR);

    //@formatter:on

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;

}
