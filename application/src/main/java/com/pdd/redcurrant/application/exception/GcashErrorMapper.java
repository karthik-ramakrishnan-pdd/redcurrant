package com.pdd.redcurrant.application.exception;

import java.util.Map;

public class GcashErrorMapper {

    private static final Map<String, WebApplicationExceptionReason> codeMap = Map.ofEntries(
            Map.entry("3", WebApplicationExceptionReason.PARTNER_WALLET_NOT_FOUND),
            Map.entry("8", WebApplicationExceptionReason.INVALID_PARTNER_WALLET_PIN),
            Map.entry("GCASH_PARSE_ERROR", WebApplicationExceptionReason.MESSAGE_NOT_READABLE_ERROR),
            Map.entry("GCASH_INVALID_RESPONSE", WebApplicationExceptionReason.GCASH_GENERIC_FAILURE));

    public static WebApplicationExceptionReason map(String gcashCode) {
        return codeMap.getOrDefault(gcashCode, WebApplicationExceptionReason.GCASH_GENERIC_FAILURE);
    }

}
