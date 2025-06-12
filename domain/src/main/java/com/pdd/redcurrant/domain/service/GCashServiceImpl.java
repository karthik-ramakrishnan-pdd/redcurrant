package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.request.BankRequestDto;
import com.pdd.redcurrant.domain.data.request.CancelTxnRequestDto;
import com.pdd.redcurrant.domain.data.request.PartnerRatesRequestDto;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.request.VostroBalEnquiryRequestDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.BankResponseDto;
import com.pdd.redcurrant.domain.data.response.CancelTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.PartnerRatesResponseDto;
import com.pdd.redcurrant.domain.data.response.PreSendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
import com.pdd.redcurrant.domain.mappers.GCashMapper;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GCashServiceImpl implements GCashServicePort {

    private final GCashMapper gCashMapper;

    @Override
    public SendTxnResponseDto sendTxn(RequestDto request) {
        return null;
    }

    @Override
    public PreSendTxnResponseDto preSendTxn(RequestDto request) {
        return null;
    }

    @Override
    public EnquiryResponseDto enquiryTxn(RequestDto request) {
        return null;
    }

    @Override
    public VostroBalEnquiryResponseDto vostroBalEnquiry(VostroBalEnquiryRequestDto request) {
        return null;
    }

    @Override
    public AccountDetailsResponseDto fetchAcctDtls(RequestDto request) {
        return null;
    }

    @Override
    public BankResponseDto bankslist(BankRequestDto request) {
        return null;
    }

    @Override
    public PartnerRatesResponseDto getPartnerRates(PartnerRatesRequestDto request) {
        return null;
    }

    @Override
    public CancelTxnResponseDto cancelTxn(CancelTxnRequestDto request) {
        return null;
    }

}