package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.MockDto;
import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SolaceServiceImpl implements SolaceServicePort {

    @Override
    public void process(String message) {
        var test = MapperUtils.convert(message, MockDto.class);
        log.info("Id - {}, Wallet Id - {}, TSP Id - {}, Transaction Id - {}", test.getId(), test.getWalletId(),
                test.getTspId(), test.getTransactionId());
    }

    @Override
    public MockDto processAndReturn(String message) {
        var test = MapperUtils.convert(message, MockDto.class);
        test.setTspId(test.getTspId().toUpperCase());
        return test;
    }
}
