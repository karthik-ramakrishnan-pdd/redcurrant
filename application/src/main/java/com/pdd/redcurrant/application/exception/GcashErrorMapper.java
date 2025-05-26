package com.pdd.redcurrant.application.exception;

import java.util.Map;

public class GcashErrorMapper {

    private static final Map<String, WebApplicationExceptionReason> codeMap = Map.ofEntries(
            Map.entry("3", WebApplicationExceptionReason.PARTNER_WALLET_NOT_FOUND),
            Map.entry("8", WebApplicationExceptionReason.INVALID_PARTNER_WALLET_PIN),
            Map.entry("10000000", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("70000000", WebApplicationExceptionReason.AUTHORIZATION_ERROR),
            Map.entry("10000011", WebApplicationExceptionReason.ACCOUNT_NOT_ACTIVE),
            Map.entry("10000012", WebApplicationExceptionReason.TARGET_NOT_KYCED),
            Map.entry("10000014", WebApplicationExceptionReason.WALLET_LIMIT_EXCEEDED),
            Map.entry("10000015", WebApplicationExceptionReason.WALLET_LIMIT_EXCEEDED),
            Map.entry("10000016", WebApplicationExceptionReason.COUNTRY_BLACKLISTED),
            Map.entry("10000017", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("10000018", WebApplicationExceptionReason.RISK_REJECTED),
            Map.entry("10000019", WebApplicationExceptionReason.COMPLIANCE_FAILURE),
            Map.entry("10000020", WebApplicationExceptionReason.INVALID_AMOUNT),
            Map.entry("10000021", WebApplicationExceptionReason.CREDIT_FAILED),
            Map.entry("10000022", WebApplicationExceptionReason.INSUFFICIENT_BALANCE),
            Map.entry("60000000", WebApplicationExceptionReason.GCASH_RETRY_LATER),
            Map.entry("60000001", WebApplicationExceptionReason.GCASH_INTERNAL_ERROR),
            Map.entry("60000002", WebApplicationExceptionReason.GCASH_INTERNAL_ERROR),
            Map.entry("10000023", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("10000024", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("10000025", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("10000026", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR),
            Map.entry("90001006", WebApplicationExceptionReason.GCASH_RETRY_LATER),
            Map.entry("90001007", WebApplicationExceptionReason.GCASH_RETRY_LATER),
            Map.entry("GCASH_INVALID_RESPONSE", WebApplicationExceptionReason.GCASH_GENERIC_FAILURE),
            Map.entry("INVALID_REQUEST_FORMAT", WebApplicationExceptionReason.REQUEST_VALIDATION_ERROR));

    public static WebApplicationExceptionReason map(String gcashCode) {
        return codeMap.getOrDefault(gcashCode, WebApplicationExceptionReason.GCASH_GENERIC_FAILURE);
    }

}
