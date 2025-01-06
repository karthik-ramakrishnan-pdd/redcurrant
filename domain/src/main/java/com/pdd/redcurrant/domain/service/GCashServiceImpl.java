package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.RequestDto;
import com.pdd.redcurrant.domain.data.ResponseDto;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GCashServiceImpl implements GCashServicePort {

    // private final GCashMapper gCashMapper;

    @Override
    public ResponseDto sendTxn(RequestDto request) {
        return null;
    }

    @Override
    public ResponseDto enquiryTxn(RequestDto request) {
        log.info("Method {}, Routing Key {}", request.getMetadata().getMethod(),
                request.getMetadata().getExchangeRoutingKey());
        return null;
    }

    @Override
    public ResponseDto vostroBalEnquiry(RequestDto request) {
        return null;
    }

    @Override
    public ResponseDto fetchAcctDtls(RequestDto request) {
        return null;
    }

    @Override
    public ResponseDto bankslist(RequestDto request) {
        return null;
    }

    @Override
    public ResponseDto getPartnerRates(RequestDto request) {
        return null;
    }

    @Override
    public ResponseDto cancelTxn(RequestDto request) {
        return null;
    }

}