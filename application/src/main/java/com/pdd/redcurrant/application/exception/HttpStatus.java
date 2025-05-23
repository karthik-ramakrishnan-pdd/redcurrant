package com.pdd.redcurrant.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpStatus {

    BAD_REQUEST(400), NOT_FOUND(404), UNAUTHORIZED(401), FORBIDDEN(403), INTERNAL_SERVER_ERROR(500),
    EXPECTATION_FAILED(417), METHOD_NOT_ALLOWED(405), UNSUPPORTED_MEDIA_TYPE(415), BAD_GATEWAY(502);

    private final Integer value;

    public static HttpStatus from(Integer value) {
        for (HttpStatus httpStatus : values()) {
            if (httpStatus.getValue().equals(value)) {
                return httpStatus;
            }
        }
        return null;
    }

}
