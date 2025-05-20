package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.ResponseDto;
import com.pdd.redcurrant.domain.data.request.BaseRequestDto;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.ports.api.ServiceRegistryPort;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class SolaceServiceImpl implements SolaceServicePort {

    private final ServiceRegistryPort serviceRegistry;

    private final Validator validator;

    @Override
    public void process(String message) {
        var request = MapperUtils.convert(message, BaseRequestDto.class);

        // Validate using javax.validation.Validator
        Set<ConstraintViolation<BaseRequestDto>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<BaseRequestDto> violation : violations) {
                sb.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException(sb.toString());
        }

        serviceRegistry.invoke(request.getRoutingKey(), request.getMethod(), request.toString());
    }

    @Override
    public ResponseDto processAndReturn(String message) {
        var request = MapperUtils.convert(message, RequestDto.class);
        log.info("Received request: {}", MapperUtils.toString(request));
        return ResponseDto.builder().statusCode("200").statusDesc("SUCCESS").build();
    }

}
