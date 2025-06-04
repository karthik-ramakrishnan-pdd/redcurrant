package com.pdd.redcurrant.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GCashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GCashResponseCapture;
import com.pdd.redcurrant.domain.constants.RcResponseTemplateEnum;
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
import com.pdd.redcurrant.domain.utils.GCashUtils;
import com.redcurrant.downstream.api.gcash.GcashBalanceApi;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
import com.redcurrant.downstream.dto.gcash.BalanceRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponse;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpStatusCodeException;

@RequiredArgsConstructor
public class GCashServiceImpl implements GCashServicePort {

    private final GcashBalanceApi gcashBalanceApi;

    private final GcashRemitApi gcashRemitApi;

    private final ObjectMapper objectMapper;

    private final GCashPropertiesConfig gcashPropertiesConfig;

    private final GCashResponseCapture gcashResponseCapture;

    @Override
    public SendTxnResponseDto sendTxn(RequestDto request) {
        try {
            PushRemittanceRequest pushRemittanceRequest = GCashMapper.toPushRemitRequest(request, gcashPropertiesConfig,
                    objectMapper);
            PushRemittanceResponse pushRemittanceResponse = gcashRemitApi.pushRemit(pushRemittanceRequest);
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GCashMapper.toSendTxnResponse(pushRemittanceResponse);
        }
        catch (RuntimeException ex) {
            SendTxnResponseDto sendTxnResponse = SendTxnResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(sendTxnResponse, ex.getMessage(), RcResponseTemplateEnum.SEND_TXN_FAILURE);
            return sendTxnResponse;
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
                    GCashMapper.toGetRemittanceStatusRequest(request, gcashPropertiesConfig, objectMapper));
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GCashMapper.toEnquiryResponse(response);
        }
        catch (RuntimeException ex) {
            EnquiryResponseDto enquiryTxnResponse = EnquiryResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(enquiryTxnResponse, ex.getMessage(),
                    RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE);
            return enquiryTxnResponse;
        }
    }

    @Override
    public VostroBalEnquiryResponseDto vostroBalEnquiry(VostroBalEnquiryRequestDto request) {
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setFromWallet(gcashPropertiesConfig.getWalletName());
        balanceRequest.setFromMpin(gcashPropertiesConfig.getWalletPin());

        try {
            return GCashMapper.toBalanceEnquiryResponse(gcashBalanceApi.getWalletBalance(balanceRequest));
        }
        catch (HttpStatusCodeException ex) {
            return GCashMapper.handleBalanceEnquiryException(ex, objectMapper);
        }
        catch (RuntimeException ex) {
            VostroBalEnquiryResponseDto balEnquiryResponse = VostroBalEnquiryResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(balEnquiryResponse, ex.getMessage(),
                    RcResponseTemplateEnum.GET_BALANCE_FAILURE);
            return balEnquiryResponse;
        }
    }

    @Override
    public AccountDetailsResponseDto fetchAcctDtls(RequestDto request) {
        try {
            ValidateAccountResponse response = gcashRemitApi
                .validateAccount(GCashMapper.toValidateAccountRequest(request, gcashPropertiesConfig, objectMapper));
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            return GCashMapper.toAccountDetailsResponse(response);
        }
        catch (RuntimeException ex) {
            AccountDetailsResponseDto accountDetailsResponse = AccountDetailsResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(accountDetailsResponse, ex.getMessage(),
                    RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE);
            return accountDetailsResponse;
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