package com.pdd.redcurrant.domain.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GcashResponseCapture;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.constants.SourceOfIncomeEnum;
import com.pdd.redcurrant.domain.data.request.common.ReceiverDto;
import com.pdd.redcurrant.domain.data.request.common.TransactionDetailsDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
import com.pdd.redcurrant.domain.utils.RsaCryptoUtils;
import com.redcurrant.downstream.dto.gcash.SenderInfo;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import com.redcurrant.downstream.dto.gcash.PushRemittanceRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceRequestRequest;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.Amount;
import com.redcurrant.downstream.dto.gcash.ComplianceInfo;
import com.redcurrant.downstream.dto.gcash.ExtendInfo;
import com.redcurrant.downstream.dto.gcash.IdInfo;
import com.redcurrant.downstream.dto.gcash.ReceiverInfo;
import com.redcurrant.downstream.dto.gcash.RequestBody;
import com.redcurrant.downstream.dto.gcash.RequestHead;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequest;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequestRequest;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusRequestRequestBody;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponse;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequestRequest;
import com.redcurrant.downstream.dto.gcash.ValidateAccountRequestRequestBody;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
import com.redcurrant.downstream.dto.gcash.ResultInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
@Slf4j
public class GcashMapper {

    private static final String GCASH_SUCCESS_RESPONSE_CODE = "S";

    private static final DateTimeFormatter DOB_RC_INPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");

    private static final DateTimeFormatter DOB_GCASH_INTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public PushRemittanceRequest toPushRemitRequest(RequestDto rcRequest, GcashPropertiesConfig gcashPropertiesConfig,
            ObjectMapper objectMapper) {
        PushRemittanceRequestRequest request = new PushRemittanceRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));
        request.setBody(buildRequestBody(rcRequest, gcashPropertiesConfig));

        PushRemittanceRequest fullRequest = new PushRemittanceRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    public RemittanceStatusRequest toGetRemittanceStatusRequest(RequestDto rcRequest,
            GcashPropertiesConfig gcashPropertiesConfig, ObjectMapper objectMapper) {
        RemittanceStatusRequestRequest request = new RemittanceStatusRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));
        request.setBody(new RemittanceStatusRequestRequestBody().requestId(rcRequest.getTransaction().getAgentRefNo())
            .remcoId(gcashPropertiesConfig.getClientId()));
        RemittanceStatusRequest fullRequest = new RemittanceStatusRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    public ValidateAccountRequest toValidateAccountRequest(RequestDto rcRequest,
            GcashPropertiesConfig gcashPropertiesConfig, ObjectMapper objectMapper) {
        ValidateAccountRequestRequest request = new ValidateAccountRequestRequest();
        request.setHead(buildRequestHead(gcashPropertiesConfig));

        Phonenumber.PhoneNumber phoneNumber = parsePhoneNumber(
                rcRequest.getTransaction().getOptedService().getBeneficiaryAccountNumber());
        String gcashAccount = phoneNumber.getCountryCode() + "-" + phoneNumber.getNationalNumber();

        request.setBody(new ValidateAccountRequestRequestBody().requestId(UUID.randomUUID().toString())
            .remcoId(gcashPropertiesConfig.getClientId())
            .gcashAccount(gcashAccount));
        ValidateAccountRequest fullRequest = new ValidateAccountRequest();
        fullRequest.setRequest(request);
        fullRequest.setSignature(signRequest(objectMapper, request, gcashPropertiesConfig));
        return fullRequest;
    }

    private String signRequest(ObjectMapper objectMapper, Object request, GcashPropertiesConfig gcashPropertiesConfig) {
        try {
            String requestBodyToSign = objectMapper.writeValueAsString(request);

            PrivateKey servicePrivateKey = RsaCryptoUtils.loadPrivateKey(gcashPropertiesConfig.getPrivateKey());
            return RsaCryptoUtils.sign(requestBodyToSign, servicePrivateKey, gcashPropertiesConfig.getKeyAlgorithm());
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException("Unable to parse GCash request");
        }
    }

    public void verifyGcashResponseSignature(ObjectMapper objectMapper, GcashPropertiesConfig config,
            GcashResponseCapture responseCapture) {
        try {
            JsonNode gcashResponse = objectMapper.readTree(responseCapture.get());
            String gcashResponseBody = objectMapper
                .writeValueAsString(gcashResponse.get(PushRemittanceResponse.JSON_PROPERTY_RESPONSE));
            String gcashSignature = gcashResponse.get(PushRemittanceResponse.JSON_PROPERTY_SIGNATURE).asText();
            PublicKey publicKey = RsaCryptoUtils.loadPublicKey(config.getPublicKey());
            boolean verified = RsaCryptoUtils.verify(gcashResponseBody, gcashSignature, publicKey,
                    config.getKeyAlgorithm());

            if (!verified) {
                throw new RuntimeException("Signature verification failed – response may be tampered");
            }
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException("Unable to parse GCash error response");
        }
    }

    private RequestHead buildRequestHead(GcashPropertiesConfig gcashPropertiesConfig) {
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
        sender.setCountryOfBirth(getIso3CountryCodeFromName(rcRequest.getSender().getSenderPlaceOfBirth()));
        sender.setDateOfBirth(convertDobToGcashFormat(rcRequest.getSender().getSenderDOB()));
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
        String currency = transactionDetailsDto.getToReceiver().getReceiveCurrency();
        if (!"PHP".equalsIgnoreCase(currency)) {
            throw new RuntimeException("Only PHP currency is supported");
        }

        String value = transactionDetailsDto.getToReceiver().getAmountToSend();
        if (!value.matches("^\\d+(\\.\\d{1,2})?$")) {
            throw new RuntimeException("Amount must have up to two decimal places (e.g., 100.00)");
        }

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value);
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
        compliance.setOriginatingCountry(getIso3CountryCodeFromIso2(
                rcRequest.getMetadata().getOrderingInstitution().getInstitutionAddress().getCountryCode()));
        compliance.setSenderInfo(buildSenderInfo(rcRequest));
        compliance.setReceiverInfo(buildReceiverInfo(rcRequest.getReceiver()));
        return compliance;
    }

    private RequestBody buildRequestBody(RequestDto rcRequest, GcashPropertiesConfig gcashPropertiesConfig) {
        RequestBody body = new RequestBody();
        body.setAction(RequestBody.ActionEnum.COMMIT);
        body.setRemcoId(gcashPropertiesConfig.getClientId());
        body.requestId(rcRequest.getTransaction().getAgentRefNo());
        body.setComplianceInfo(buildComplianceInfo(rcRequest));
        body.setExtendInfo(buildExtendInfo(rcRequest));
        body.setAmount(buildAmount(rcRequest.getTransaction()));
        Phonenumber.PhoneNumber phoneNumber = parsePhoneNumber(
                rcRequest.getTransaction().getOptedService().getBeneficiaryAccountNumber());
        body.setGcashAccountCountryCode(String.valueOf(phoneNumber.getCountryCode()));
        body.setGcashAccount(String.valueOf(phoneNumber.getNationalNumber()));
        return body;
    }

    private Phonenumber.PhoneNumber parsePhoneNumber(String phoneNumber) {
        try {
            return PhoneNumberUtil.getInstance().parse(phoneNumber, null);
        }
        catch (NumberParseException ex) {
            throw new RuntimeException("Invalid phone number format: " + phoneNumber);
        }
    }

    private String convertDobToGcashFormat(String dateOfBirth) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateOfBirth, DOB_RC_INPUT_FORMAT);
            return dateTime.format(DOB_GCASH_INTPUT_FORMAT);
        }
        catch (DateTimeParseException ex) {
            throw new RuntimeException("Invalid date of birth format: " + dateOfBirth);
        }
    }

    private String getIso3CountryCodeFromName(String countryName) {
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            if (locale.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(countryName)) {
                return locale.getISO3Country();
            }
        }
        throw new RuntimeException("Unknown country name: " + countryName);
    }

    private String getIso3CountryCodeFromIso2(String iso2CountryCode) {
        return Locale.of("", iso2CountryCode).getISO3Country();
    }

    public VostroBalEnquiryResponseDto handleBalEnquiryException(HttpStatusCodeException ex,
            ObjectMapper objectMapper) {
        try {
            BalanceResponse response = objectMapper.readValue(ex.getResponseBodyAsString(), BalanceResponse.class);
            return toBalanceResponse(response);
        }
        catch (JsonProcessingException parseEx) {
            throw new RuntimeException("Unable to parse GCash error response");
        }
    }

    public static SendTxnResponseDto toSendTxnResponse(PushRemittanceResponse response) {
        boolean success = GCASH_SUCCESS_RESPONSE_CODE
            .equalsIgnoreCase(response.getResponse().getBody().getResultInfo().getResultStatus());

        return SendTxnResponseDto.builder()
            .statusCode(success ? "0" : "100")
            .statusDescription(success ? "Success" : "Error")
            .returnCode(success ? "1" : "101")
            .returnDescription(buildDescription(response.getResponse().getBody().getResultInfo()))
            .partnerRefNo(success ? response.getResponse().getBody().getTransactionId() : null)
            .build();
    }

    public static EnquiryResponseDto toEnquiryResponse(RemittanceStatusResponse response) {
        boolean success = GCASH_SUCCESS_RESPONSE_CODE
            .equalsIgnoreCase(response.getResponse().getBody().getResultInfo().getResultStatus());

        return EnquiryResponseDto.builder()
            .statusCode(success ? "0" : "100")
            .statusDescription(success ? "Success" : "Error")
            .returnCode(success ? "2" : "101")
            .returnDescription(buildDescription(response.getResponse().getBody().getResultInfo()))
            .build();
    }

    public static AccountDetailsResponseDto toAccountDetailsResponse(ValidateAccountResponse response) {
        boolean success = GCASH_SUCCESS_RESPONSE_CODE
            .equalsIgnoreCase(response.getResponse().getBody().getResultInfo().getResultStatus());

        return AccountDetailsResponseDto.builder()
            .statusCode(success ? "0" : "100")
            .statusDescription(success ? "Success" : "Info")
            .returnCode(success ? "2" : "-1")
            .returnDescription(buildDescription(response.getResponse().getBody().getResultInfo()))
            .build();
    }

    public static VostroBalEnquiryResponseDto toBalanceResponse(BalanceResponse response) {
        boolean success = "0".equalsIgnoreCase(response.getCode())
                && (response.getBalance() != null || !response.getBalance().getAvailableBalance().isEmpty());
        return VostroBalEnquiryResponseDto.builder()
            .statusCode(success ? "0" : "100")
            .statusDescription(success ? "Success" : "Error")
            .returnCode(success ? "1" : "101")
            .returnDescription(String.format("%s|%s", response.getCode(), response.getMessage()))
            .balance(success ? response.getBalance().getAvailableBalance() : null)
            .build();
    }

    private static String buildDescription(ResultInfo info) {
        return String.join("|", Objects.toString(info.getResultCodeId(), ""),
                Objects.toString(info.getResultCode(), ""), Objects.toString(info.getResultMsg(), ""));
    }

}
