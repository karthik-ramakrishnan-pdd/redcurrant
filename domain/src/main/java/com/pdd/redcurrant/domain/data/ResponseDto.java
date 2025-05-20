package com.pdd.redcurrant.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {

    private String reqId;

    private String txnRefNum;

    private String statusCode;

    private String statusDesc;

    private String returnCode;

    private String returnDesc;

}
