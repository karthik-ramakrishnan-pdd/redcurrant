package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.MockDto;
import com.pdd.redcurrant.domain.data.RequestDto;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.registry.ServicePortRegistry;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SolaceServiceImpl implements SolaceServicePort {

    private final ServicePortRegistry servicePortRegistry;

    @Override
    public void process(String message) {
        var request = MapperUtils.convert(message, RequestDto.class);

        if (request == null || request.getMetadata() == null || request.getMetadata().getMethod() == null
                || request.getMetadata().getExchangeRoutingKey() == null) {
            throw new IllegalArgumentException("Missing Mandatory parameters");
        }

        servicePortRegistry.invokeMethod(request.getMetadata().getExchangeRoutingKey(),
                request.getMetadata().getMethod(), request);
    }

    @Override
    public MockDto processAndReturn(String message) {
        var test = MapperUtils.convert(message, MockDto.class);
        test.setTspId(test.getTspId().toUpperCase());
        return test;
    }

}
