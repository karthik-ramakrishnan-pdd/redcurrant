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
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

@RequiredArgsConstructor
@Slf4j
public class GCashServiceImpl implements GCashServicePort {

    private final GcashBalanceApi gcashBalanceApi;

    private final GcashRemitApi gcashRemitApi;

    private final ObjectMapper objectMapper;

    private final GCashPropertiesConfig gcashPropertiesConfig;

    private final GCashResponseCapture gcashResponseCapture;

    @Override
    public SendTxnResponseDto sendTxn(RequestDto request) {
        try {
            log.info("Initiating sendTxn for requestId: {}", request.getReqId());
            PushRemittanceRequest pushRemittanceRequest = GCashMapper.toPushRemitRequest(request, gcashPropertiesConfig,
                    objectMapper);
            log.debug("PushRemittanceRequest: {}", pushRemittanceRequest);
            PushRemittanceResponse pushRemittanceResponse = gcashRemitApi.pushRemit(pushRemittanceRequest);
            log.debug("PushRemittanceResponse received: {}", pushRemittanceResponse);
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            log.info("GCash response signature verified for requestId: {}", request.getReqId());
            return GCashMapper.toSendTxnResponse(pushRemittanceResponse, request);
        }
        catch (RuntimeException ex) {
            log.error("sendTxn failed for requestId: {}, error: {}", request.getReqId(), ex.getMessage(), ex);
            SendTxnResponseDto sendTxnResponse = SendTxnResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(sendTxnResponse, ex.getMessage(), RcResponseTemplateEnum.SEND_TXN_FAILURE,
                    request);
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
            log.info("Initiating enquiryTxn for requestId: {}", request.getReqId());
            RemittanceStatusRequest remittanceStatusRequest = GCashMapper.toGetRemittanceStatusRequest(request,
                    gcashPropertiesConfig, objectMapper);
            log.debug("RemittanceStatusRequest: {}", remittanceStatusRequest);
            RemittanceStatusResponse response = gcashRemitApi.getTransactionStatus(remittanceStatusRequest);
            log.debug("RemittanceStatusResponse: {}", response);
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            log.info("GCash response signature verified for enquiryTxn, requestId: {}", request.getReqId());
            return GCashMapper.toEnquiryResponse(response, request);
        }
        catch (RuntimeException ex) {
            log.error("enquiryTxn failed for requestId: {}, error: {}", request.getReqId(), ex.getMessage(), ex);
            EnquiryResponseDto enquiryTxnResponse = EnquiryResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(enquiryTxnResponse, ex.getMessage(),
                    RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE, request);
            return enquiryTxnResponse;
        }
    }

    @Override
    public VostroBalEnquiryResponseDto vostroBalEnquiry(VostroBalEnquiryRequestDto request) {
        log.info("Initiating vostroBalEnquiry for requestId: {}", request.getReqId());
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setFromWallet(gcashPropertiesConfig.getWalletName());
        balanceRequest.setFromMpin(gcashPropertiesConfig.getWalletPin());

        try {
            BalanceResponse response = gcashBalanceApi.getWalletBalance(balanceRequest);
            log.debug("BalanceResponse received: {}", response);
            return GCashMapper.toBalanceEnquiryResponse(response, request);
        }
        catch (HttpStatusCodeException ex) {
            log.error("HttpStatusCodeException during vostroBalEnquiry: {}", ex.getMessage(), ex);
            return GCashMapper.handleBalanceEnquiryException(ex, objectMapper, request);
        }
        catch (RuntimeException ex) {
            log.error("Unexpected error during vostroBalEnquiry: {}", ex.getMessage(), ex);
            VostroBalEnquiryResponseDto balEnquiryResponse = VostroBalEnquiryResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(balEnquiryResponse, ex.getMessage(),
                    RcResponseTemplateEnum.GET_BALANCE_FAILURE, request);
            return balEnquiryResponse;
        }
    }

    @Override
    public AccountDetailsResponseDto fetchAcctDtls(RequestDto request) {
        try {
            log.info("Initiating fetchAcctDtls for requestId: {}", request.getReqId());
            ValidateAccountRequest validateAccountRequest = GCashMapper.toValidateAccountRequest(request,
                    gcashPropertiesConfig, objectMapper);
            log.debug("ValidateAccountRequest: {}", validateAccountRequest);
            ValidateAccountResponse response = gcashRemitApi.validateAccount(validateAccountRequest);
            log.debug("ValidateAccountResponse: {}", response);
            GCashUtils.verifyGcashResponseSignature(objectMapper, gcashPropertiesConfig, gcashResponseCapture);
            log.info("GCash response signature verified for fetchAcctDtls, requestId: {}", request.getReqId());
            return GCashMapper.toAccountDetailsResponse(response, request);
        }
        catch (RuntimeException ex) {
            log.error("fetchAcctDtls failed for requestId: {}, error: {}", request.getReqId(), ex.getMessage(), ex);
            AccountDetailsResponseDto accountDetailsResponse = AccountDetailsResponseDto.builder().build();
            GCashUtils.mapToBaseResponse(accountDetailsResponse, ex.getMessage(),
                    RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE, request);
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