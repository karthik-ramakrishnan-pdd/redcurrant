package com.pdd.redcurrant.domain.data;

import lombok.Data;

@Data
public class ResponseDto {

    private String reqId;

    private String txnRefNum;

    private String statusCode;

    private String statusDesc;

    private String returnCode;

    private String returnDesc;

}
