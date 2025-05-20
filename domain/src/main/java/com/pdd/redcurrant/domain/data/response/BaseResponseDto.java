package com.pdd.redcurrant.domain.data.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseResponseDto {

    private String statusCode;

    private String statusDescription;

    private String returnCode;

    private String returnDescription;

    private String reqId;

}