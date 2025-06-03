package com.pdd.redcurrant.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GcashResponseCapture;
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

import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponse;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
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

    private final GcashResponseCapture gcashResponseCapture;

    @Override
    public SendTxnResponseDto sendTxn(RequestDto request) {
        try {
            PushRemittanceResponse response = gcashRemitApi
                .pushRemit(GcashMapper.toPushRemitRequest(request, gcashPropertiesConfig, objectMapper));
            GcashMapper.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GcashMapper.toSendTxnResponse(response);
        }
        catch (RuntimeException ex) {
            return SendTxnResponseDto.builder()
                .statusCode("100")
                .statusDescription("Error")
                .returnCode("101")
                .returnDescription(ex.getMessage())
                .build();
        }

    }

    @Override
    public PreSendTxnResponseDto preSendTxn(RequestDto request) {
        return null;
    }

    @Override
    public EnquiryResponseDto enquiryTxn(RequestDto request) {
        try {
            RemittanceStatusResponse response = gcashRemitApi.getTransactionStatus(
                    GcashMapper.toGetRemittanceStatusRequest(request, gcashPropertiesConfig, objectMapper));
            GcashMapper.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GcashMapper.toEnquiryResponse(response);
        }
        catch (RuntimeException ex) {
            return EnquiryResponseDto.builder()
                .statusCode("100")
                .statusDescription("Error")
                .returnCode("101")
                .returnDescription(ex.getMessage())
                .build();
        }
    }

    @Override
    public VostroBalEnquiryResponseDto vostroBalEnquiry(VostroBalEnquiryRequestDto request) {
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setFromWallet(gcashPropertiesConfig.getWalletName());
        balanceRequest.setFromMpin(gcashPropertiesConfig.getWalletPin());

        try {
            return GcashMapper.toBalanceResponse(gcashBalanceApi.getWalletBalance(balanceRequest));
        }
        catch (HttpStatusCodeException ex) {
            return GcashMapper.handleBalEnquiryException(ex, objectMapper);
        }
        catch (RuntimeException ex) {
            return VostroBalEnquiryResponseDto.builder()
                .statusCode("100")
                .statusDescription("Error")
                .returnCode("101")
                .returnDescription(ex.getMessage())
                .build();
        }
    }

    @Override
    public AccountDetailsResponseDto fetchAcctDtls(RequestDto request) {
        try {
            ValidateAccountResponse response = gcashRemitApi
                .validateAccount(GcashMapper.toValidateAccountRequest(request, gcashPropertiesConfig, objectMapper));
            GcashMapper.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GcashMapper.toAccountDetailsResponse(response);
        }
        catch (RuntimeException ex) {
            return AccountDetailsResponseDto.builder()
                .statusCode("100")
                .statusDescription("Error")
                .returnCode("101")
                .returnDescription(ex.getMessage())
                .build();
        }
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