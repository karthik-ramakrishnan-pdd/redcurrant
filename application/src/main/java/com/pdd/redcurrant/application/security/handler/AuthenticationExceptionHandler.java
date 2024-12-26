package com.pdd.redcurrant.application.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.application.exception.ErrorResponseDto;
import com.pdd.redcurrant.application.exception.WebApplicationExceptionReason;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles authentication exceptions.
 */
@Component
@ConditionalOnClass(AuthenticationEntryPoint.class)
@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Handles {@link AuthenticationException} exception thrown by Spring Security.
     * @param request the request
     * @param response the response
     * @param ex the exception
     * @throws IOException if getting the output stream from the response fails
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ex) throws IOException {
        log.warn("Unauthorized access - {}", ex.getMessage());
        final ErrorResponseDto errorResponse = ErrorResponseDto.builder()
            .code(WebApplicationExceptionReason.AUTHENTICATION_ERROR.getCode())
            .message(String.format(WebApplicationExceptionReason.AUTHENTICATION_ERROR.getMessage(), ex.getMessage()))
            .status(WebApplicationExceptionReason.AUTHENTICATION_ERROR.getHttpStatus())
            .timestamp(LocalDateTime.now())
            .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(WebApplicationExceptionReason.AUTHENTICATION_ERROR.getHttpStatus().getValue());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), errorResponse);
        response.getOutputStream().flush();
    }

}
