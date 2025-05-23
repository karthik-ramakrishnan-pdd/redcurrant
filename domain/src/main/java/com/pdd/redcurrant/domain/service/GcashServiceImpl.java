package com.pdd.redcurrant.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
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
import com.pdd.redcurrant.domain.mappers.GcashMapper;
import com.pdd.redcurrant.domain.ports.api.GcashServicePort;
import com.redcurrant.downstream.api.gcash.GcashBalanceApi;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
import com.redcurrant.downstream.dto.gcash.BalanceRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

@RequiredArgsConstructor
@Slf4j
public class GcashServiceImpl implements GcashServicePort {

    private final GcashBalanceApi gcashBalanceApi;

    private final GcashRemitApi gcashRemitApi;

    private final ObjectMapper objectMapper;

    private final GcashPropertiesConfig gcashPropertiesConfig;

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
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setFromWallet(gcashPropertiesConfig.getWalletName());
        balanceRequest.setFromMpin(gcashPropertiesConfig.getWalletPin());

        try {
            return GcashMapper.of(gcashBalanceApi.getWalletBalance(balanceRequest));
        }
        catch (HttpStatusCodeException ex) {
            return GcashMapper.handleBalEnquiryException(ex, objectMapper);
        }
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