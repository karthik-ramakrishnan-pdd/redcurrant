package com.pdd.redcurrant.application.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pdd.redcurrant.domain.annotations.StandardJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
public class ErrorResponseDto {

    private String traceId;

    private String code;

    private String message;

    private String debugMessage;

    @JsonIgnore
    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    private List<InvalidParameterDto> invalidParameters;

    private Object errors;

    public ErrorResponseDto(ExceptionPolicy policy) {
        code = policy.getCode();
        message = policy.getMessage();
        status = policy.getHttpStatus();
    }

}
