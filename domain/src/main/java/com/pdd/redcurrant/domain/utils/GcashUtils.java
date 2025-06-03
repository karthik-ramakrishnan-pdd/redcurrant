package com.pdd.redcurrant.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GcashResponseCapture;
import com.pdd.redcurrant.domain.constants.RcResponseTemplateEnum;
import com.pdd.redcurrant.domain.data.response.BaseResponseDto;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.ResultInfo;
import lombok.experimental.UtilityClass;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

@UtilityClass
public class GcashUtils {

    private static final String GCASH_REMIT_SUCCESS_RESPONSE_CODE = "S";

    private static final String GCASH_BALANCE_SUCCESS_RESPONSE_CODE = "0";

    private static final String DOB_RC_INPUT_FORMAT = "MMM dd yyyy HH:mm:ss";

    private static final String DOB_GCASH_INTPUT_FORMAT = "yyyyMMdd";

    public String signRequest(ObjectMapper objectMapper, Object request, GcashPropertiesConfig gcashPropertiesConfig) {
        try {
            String requestBodyToSign = objectMapper.writeValueAsString(request).replace("'", "\\u0027");

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
                .writeValueAsString(gcashResponse.get(PushRemittanceResponse.JSON_PROPERTY_RESPONSE))
                .replace("'", "\\u0027");
            String gcashSignature = gcashResponse.get(PushRemittanceResponse.JSON_PROPERTY_SIGNATURE).asText();
            PublicKey publicKey = RsaCryptoUtils.loadPublicKey(config.getPublicKey());
            boolean verified = RsaCryptoUtils.verify(gcashResponseBody, gcashSignature, publicKey,
                    config.getKeyAlgorithm());

            if (!verified) {
                throw new RuntimeException("Signature verification failed");
            }
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException("Unable to parse GCash error response");
        }
    }

    public boolean isSuccessful(ResultInfo resultInfo) {
        return resultInfo != null && GCASH_REMIT_SUCCESS_RESPONSE_CODE.equalsIgnoreCase(resultInfo.getResultStatus());
    }

    public boolean isSuccessful(BalanceResponse response) {
        return GCASH_BALANCE_SUCCESS_RESPONSE_CODE.equalsIgnoreCase(response.getCode())
                && (response.getBalance() != null || !response.getBalance().getAvailableBalance().isEmpty());

    }

    public void mapToBaseResponse(BaseResponseDto response, String description, RcResponseTemplateEnum template) {
        response.setStatusCode(template.statusCode);
        response.setStatusDescription(template.statusDescription);
        response.setReturnCode(template.returnCode);
        response.setReturnDescription(description);
    }

    public void mapToBaseResponse(BaseResponseDto response, ResultInfo resultInfo, RcResponseTemplateEnum template) {
        mapToBaseResponse(response, buildErrorDescription(resultInfo), template);
    }

    public void mapToBaseResponse(BaseResponseDto response, BalanceResponse balanceResponse,
            RcResponseTemplateEnum template) {
        mapToBaseResponse(response, buildErrorDescription(balanceResponse), template);
    }

    private String buildErrorDescription(ResultInfo info) {
        return String.join("|", Objects.toString(info.getResultCodeId(), ""),
                Objects.toString(info.getResultCode(), ""), Objects.toString(info.getResultMsg(), ""));
    }

    private String buildErrorDescription(BalanceResponse response) {
        return String.join("|", Objects.toString(response.getCode(), ""), Objects.toString(response.getMessage(), ""));
    }

    public String getFormattedSenderDateOfBirth(String senderDOB) {
        return CommonUtils.convertDateTimeFormat(senderDOB, DOB_RC_INPUT_FORMAT, DOB_GCASH_INTPUT_FORMAT);
    }

}
