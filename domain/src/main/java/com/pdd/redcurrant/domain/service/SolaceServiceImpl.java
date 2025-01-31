package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.RequestDto;
import com.pdd.redcurrant.domain.data.ResponseDto;
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
    public ResponseDto processAndReturn(String message) {
        var request = MapperUtils.convert(message, RequestDto.class);
        log.info("Received request: {}", MapperUtils.toString(request));
        return ResponseDto.builder().statusCode("200").statusDesc("SUCCESS").build();
    }

}
