package com.pdd.redcurrant.domain.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto {

    private String reqId;

    private String txnRefNum;

    private String statusCode;

    private String statusDesc;

    private String returnCode;

    private String returnDesc;

}
