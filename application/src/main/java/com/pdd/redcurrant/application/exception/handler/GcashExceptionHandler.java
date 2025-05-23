package com.pdd.redcurrant.application.exception.handler;

import com.pdd.redcurrant.application.exception.ErrorResponseDto;
import com.pdd.redcurrant.application.exception.WebApplicationExceptionReason;
import com.pdd.redcurrant.application.exception.GcashErrorMapper;
import com.pdd.redcurrant.domain.exception.GcashException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GcashExceptionHandler {

    @ExceptionHandler(GcashException.class)
    public ResponseEntity<ErrorResponseDto> handleGcashDomainException(GcashException ex) {
        WebApplicationExceptionReason reason = GcashErrorMapper.map(ex.getErrorCode());

        ErrorResponseDto response = ErrorResponseDto.builder()
            .code(reason.getCode())
            .message(String.format(reason.getMessage(), ex.getMessage()))
            .status(reason.getHttpStatus())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(reason.getHttpStatus().getValue()).body(response);
    }

}
