package com.pdd.redcurrant.domain.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GCashPropertiesConfig;
import com.pdd.redcurrant.domain.constants.RcResponseTemplateEnum;
import com.pdd.redcurrant.domain.constants.SourceOfIncomeEnum;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.request.common.ReceiverDto;
import com.pdd.redcurrant.domain.data.request.common.TransactionDetailsDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
import com.pdd.redcurrant.domain.utils.CommonUtils;
import com.pdd.redcurrant.domain.utils.GCashUtils;
import com.redcurrant.downstream.dto.gcash.Amount;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import com.redcurrant.downstream.dto.gcash.ComplianceInfo;
import com.redcurrant.downstream.dto.gcash.ExtendInfo;
import com.redcurrant.downstream.dto.gcash.IdInfo;
import com.redcurrant.downstream.dto.gcash.PushRemittanceRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceRequestRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.ReceiverInfo;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequest;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequestRequest;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequestRequestBody;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponse;
import com.redcurrant.downstream.dto.gcash.RequestBody;
import com.redcurrant.downstream.dto.gcash.RequestHead;
import com.redcurrant.downstream.dto.gcash.SenderInfo;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequestRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequestRequestBody;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
import lombok.experimental.UtilityClass;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@UtilityClass
public class GCashMapper {

    public PushRemittanceRequest toPushRemitRequest(RequestDto rcRequest, GCashPropertiesConfig gcashPropertiesConfig,
            ObjectMapper objectMapper) {
        PushRemittanceRequestRequest request = new PushRemittanceRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));
        request.setBody(buildRequestBody(rcRequest, gcashPropertiesConfig));

        PushRemittanceRequest fullRequest = new PushRemittanceRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(GCashUtils.signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    public RemittanceStatusRequest toGetRemittanceStatusRequest(RequestDto rcRequest,
            GCashPropertiesConfig gcashPropertiesConfig, ObjectMapper objectMapper) {
        RemittanceStatusRequestRequest request = new RemittanceStatusRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));
        request.setBody(new RemittanceStatusRequestRequestBody().requestId(rcRequest.getTransaction().getAgentRefNo())
            .remcoId(gcashPropertiesConfig.getClientId()));

        RemittanceStatusRequest fullRequest = new RemittanceStatusRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(GCashUtils.signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    public ValidateAccountRequest toValidateAccountRequest(RequestDto rcRequest,
            GCashPropertiesConfig gcashPropertiesConfig, ObjectMapper objectMapper) {
        ValidateAccountRequestRequest request = new ValidateAccountRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));

        String phoneNumber = rcRequest.getTransaction().getOptedService().getBeneficiaryAccountNumber();
        String gcashAccount = CommonUtils.parsePhoneCountryCode(phoneNumber) + "-"
                + CommonUtils.parsePhoneNationalNumber(phoneNumber);

        request.setBody(new ValidateAccountRequestRequestBody().requestId(UUID.randomUUID().toString())
            .remcoId(gcashPropertiesConfig.getClientId())
            .gcashAccount(gcashAccount));
        ValidateAccountRequest fullRequest = new ValidateAccountRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(GCashUtils.signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    private RequestHead buildRequestHead(GCashPropertiesConfig gcashPropertiesConfig) {
        RequestHead head = new RequestHead();
        head.setVersion(gcashPropertiesConfig.getHeadVersion());
        head.setFunction(gcashPropertiesConfig.getHeadFunction());
        head.clientId(gcashPropertiesConfig.getClientId());
        head.clientSecret(gcashPropertiesConfig.getClientSecret());
        head.reqTime(Instant.now());
        head.reqMsgId(UUID.randomUUID().toString());
        head.setReserve(new HashMap<>());
        return head;
    }

    private SenderInfo buildSenderInfo(RequestDto rcRequest) {
        IdInfo idInfo = new IdInfo();
        idInfo.setIdType(IdInfo.IdTypeEnum.NATIONAL_ID);
        idInfo.setIdNumber(rcRequest.getSender().getId().getSenderIdNo());

        SenderInfo sender = new SenderInfo();
        sender.setFirstName(rcRequest.getSender().getSenderFirstName());
        sender.setMiddleName(rcRequest.getSender().getSenderMiddleName());
        sender.setLastName(rcRequest.getSender().getSenderLastName());
        sender.setCountryOfBirth(CommonUtils.mapCountryNameToIso3(rcRequest.getSender().getSenderPlaceOfBirth()));
        sender.setDateOfBirth(GCashUtils.getFormattedSenderDateOfBirth(rcRequest.getSender().getSenderDOB()));
        sender.setRelationToReceiver(rcRequest.getSender().getSenderBeneRelationship());
        sender
            .setSourceOfIncome(SourceOfIncomeEnum.fromCodeOrDefault(rcRequest.getTransaction().getSourceOfIncomeCode())
                .getDescription());
        sender.setIdInfo(idInfo);
        return sender;
    }

    private ReceiverInfo buildReceiverInfo(ReceiverDto receiverDto) {
        ReceiverInfo receiver = new ReceiverInfo();
        receiver.setFirstName(receiverDto.getBeneficiaryFirstName());
        receiver.setMiddleName(receiverDto.getBeneficiaryMiddleName());
        receiver.setLastName(receiverDto.getBeneficiaryLastName());
        return receiver;
    }

    private Amount buildAmount(TransactionDetailsDto transactionDetailsDto) {
        Amount amount = new Amount();
        amount.setCurrency(transactionDetailsDto.getToReceiver().getReceiveCurrency());
        amount.setValue(transactionDetailsDto.getToReceiver().getAmountToSend());
        return amount;
    }

    private ExtendInfo buildExtendInfo(RequestDto rcRequest) {
        ExtendInfo extendInfo = new ExtendInfo();
        extendInfo.setStoreLocation(
                rcRequest.getMetadata().getOrderingInstitution().getInstitutionAddress().getCountryCode());
        extendInfo.setRefNumber(rcRequest.getTransaction().getTxnRefNo());
        return extendInfo;
    }

    private ComplianceInfo buildComplianceInfo(RequestDto rcRequest) {
        ComplianceInfo compliance = new ComplianceInfo();
        compliance.setOriginatingCountry(CommonUtils.mapIso2CountryCodeToIso3(
                rcRequest.getMetadata().getOrderingInstitution().getInstitutionAddress().getCountryCode()));
        compliance.setSenderInfo(buildSenderInfo(rcRequest));
        compliance.setReceiverInfo(buildReceiverInfo(rcRequest.getReceiver()));
        return compliance;
    }

    private RequestBody buildRequestBody(RequestDto rcRequest, GCashPropertiesConfig gcashPropertiesConfig) {
        RequestBody body = new RequestBody();
        body.setAction(RequestBody.ActionEnum.COMMIT);
        body.setRemcoId(gcashPropertiesConfig.getClientId());
        body.requestId(rcRequest.getTransaction().getAgentRefNo());
        body.setComplianceInfo(buildComplianceInfo(rcRequest));
        body.setExtendInfo(buildExtendInfo(rcRequest));
        body.setAmount(buildAmount(rcRequest.getTransaction()));
        String phoneNumber = rcRequest.getTransaction().getOptedService().getBeneficiaryAccountNumber();
        body.setGcashAccountCountryCode(CommonUtils.parsePhoneCountryCode(phoneNumber));
        body.setGcashAccount(CommonUtils.parsePhoneNationalNumber(phoneNumber));
        return body;
    }

    public SendTxnResponseDto toSendTxnResponse(PushRemittanceResponse response) {
        boolean success = GCashUtils.isSuccessful(response.getResponse().getBody().getResultInfo());
        RcResponseTemplateEnum template = success ? RcResponseTemplateEnum.SEND_TXN_SUCCESS
                : RcResponseTemplateEnum.SEND_TXN_FAILURE;

        SendTxnResponseDto responseDto = SendTxnResponseDto.builder()
            .partnerRefNo(response.getResponse().getBody().getTransactionId())
            .build();
        GCashUtils.mapToBaseResponse(responseDto, response.getResponse().getBody().getResultInfo(), template);
        return responseDto;
    }

    public EnquiryResponseDto toEnquiryResponse(RemittanceStatusResponse response) {
        boolean success = GCashUtils.isSuccessful(response.getResponse().getBody().getResultInfo());
        RcResponseTemplateEnum template = success ? RcResponseTemplateEnum.ENQUIRY_TXN_SUCCESS
                : RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE;

        EnquiryResponseDto responseDto = EnquiryResponseDto.builder().build();
        GCashUtils.mapToBaseResponse(responseDto, response.getResponse().getBody().getResultInfo(), template);
        return responseDto;
    }

    public AccountDetailsResponseDto toAccountDetailsResponse(ValidateAccountResponse response) {
        boolean success = GCashUtils.isSuccessful(response.getResponse().getBody().getResultInfo());
        RcResponseTemplateEnum template = success ? RcResponseTemplateEnum.ACCOUNT_DETAILS_SUCCESS
                : RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE;

        AccountDetailsResponseDto responseDto = AccountDetailsResponseDto.builder().build();
        GCashUtils.mapToBaseResponse(responseDto, response.getResponse().getBody().getResultInfo(), template);
        return responseDto;
    }

    public VostroBalEnquiryResponseDto handleBalanceEnquiryException(HttpStatusCodeException ex,
            ObjectMapper objectMapper) {
        try {
            BalanceResponse response = objectMapper.readValue(ex.getResponseBodyAsString(), BalanceResponse.class);
            return toBalanceEnquiryResponse(response);
        }
        catch (JsonProcessingException parseEx) {
            throw new RuntimeException("Unable to parse GCash error response");
        }
    }

    public VostroBalEnquiryResponseDto toBalanceEnquiryResponse(BalanceResponse response) {
        boolean success = GCashUtils.isSuccessful(response);
        RcResponseTemplateEnum template = success ? RcResponseTemplateEnum.GET_BALANCE_SUCCESS
                : RcResponseTemplateEnum.GET_BALANCE_FAILURE;

        VostroBalEnquiryResponseDto responseDto = VostroBalEnquiryResponseDto.builder()
            .balance(success ? response.getBalance().getAvailableBalance() : null)
            .build();
        GCashUtils.mapToBaseResponse(responseDto, response, template);
        return responseDto;
    }

}
