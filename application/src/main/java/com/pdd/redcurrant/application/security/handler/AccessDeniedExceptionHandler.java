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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles access denied exceptions.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnClass(AccessDeniedHandler.class)
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Handles {@link AccessDeniedException} exception thrown by Spring Security.
     * @param request the request
     * @param response the response
     * @param ex the exception
     * @throws IOException if getting the output stream from the response fails
     */
    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException ex) throws IOException {
        log.warn("Forbidden access - {}", ex.getMessage());
        final ErrorResponseDto errorResponse = ErrorResponseDto.builder()
            .code(WebApplicationExceptionReason.AUTHORIZATION_ERROR.getCode())
            .message(String.format(WebApplicationExceptionReason.AUTHORIZATION_ERROR.getMessage(), ex.getMessage()))
            .status(WebApplicationExceptionReason.AUTHORIZATION_ERROR.getHttpStatus())
            .timestamp(LocalDateTime.now())
            .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(WebApplicationExceptionReason.AUTHORIZATION_ERROR.getHttpStatus().getValue());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), errorResponse);
        response.getOutputStream().flush();
    }

}
