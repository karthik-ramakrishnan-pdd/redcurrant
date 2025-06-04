package com.pdd.redcurrant.domain.exception;

import com.pdd.redcurrant.domain.data.response.BaseResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessException {

    //@formatter:off

    INTERNAL_ERROR("100", "Internal Error", "101"),
    VALIDATION_FAILED("100", "Validation Error", "101"),
    INVALID_METHOD("100", "Provided Invalid Method", "101"),
    INVALID_ROUTING_KEY("100", "Provided Invalid Routing Key", "101"),
    ;

    //@formatter:on

    public final String statusCode;

    public final String statusDescription;

    public final String returnCode;

    public BaseResponseDto toResponse(String returnDescription, String reqId) {
        return BaseResponseDto.builder()
            .statusCode(statusCode)
            .statusDescription(statusDescription)
            .returnCode(returnCode)
            .returnDescription(returnDescription)
            .reqId(reqId)
            .build();
    }

}
