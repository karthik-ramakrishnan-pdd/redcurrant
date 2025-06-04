package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.request.BaseRequestDto;
import com.pdd.redcurrant.domain.exception.BusinessException;
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
    public Object process(String message) {
        var request = MapperUtils.convert(message, BaseRequestDto.class);

        // Validate using javax.validation.Validator
        Set<ConstraintViolation<BaseRequestDto>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<BaseRequestDto> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                    .append(" ")
                    .append(violation.getMessage())
                    .append("; ");
            }
            return BusinessException.VALIDATION_FAILED.toResponse(errorMessage.toString(), request.getReqId());
        }

        return serviceRegistry.invoke(request.getRoutingKey(), request.getMethod(), message);
    }

}
