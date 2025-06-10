package com.pdd.redcurrant.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RcResponseTemplateEnum {

    SEND_TXN_SUCCESS("0", "Success", "1"), SEND_TXN_FAILURE("100", "Error", "101"),

    ENQUIRY_TXN_SUCCESS("0", "Success", "2"), ENQUIRY_TXN_FAILURE("100", "Error", "101"),

    ACCOUNT_DETAILS_SUCCESS("0", "Success", "2"), ACCOUNT_DETAILS_FAILURE("100", "Info", "-1"),

    GET_BALANCE_SUCCESS("0", "Success", "1"), GET_BALANCE_FAILURE("100", "Error", "101");

    public final String statusCode;

    public final String statusDescription;

    public final String returnCode;

}
