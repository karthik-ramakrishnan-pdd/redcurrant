package com.pdd.redcurrant.domain.exception;

import lombok.Getter;

@Getter
public class GcashException extends RuntimeException {

    private final String errorCode;

    public GcashException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
