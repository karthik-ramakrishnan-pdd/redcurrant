package com.pdd.redcurrant.domain.data.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerRatesResponseDto {

    private String statusCode;

    private String statusDescription;

    private String returnCode;

    private ReturnDescriptionDto returnDescription;

    private String reqId;

    @JsonAlias("txnRefNum")
    private String txnRefNo;

}