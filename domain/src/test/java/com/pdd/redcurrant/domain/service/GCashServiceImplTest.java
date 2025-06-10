package com.pdd.redcurrant.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.configuration.GCashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GCashResponseCapture;
import com.pdd.redcurrant.domain.constants.RcResponseTemplateEnum;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.request.VostroBalEnquiryRequestDto;
import com.pdd.redcurrant.domain.data.request.common.IdDto;
import com.pdd.redcurrant.domain.data.request.common.MetadataDto;
import com.pdd.redcurrant.domain.data.request.common.SenderDto;
import com.pdd.redcurrant.domain.data.request.common.SenderReceiverDto;
import com.pdd.redcurrant.domain.data.request.common.ReceiverDto;
import com.pdd.redcurrant.domain.data.request.common.ServiceInfoDto;
import com.pdd.redcurrant.domain.data.request.common.TransactionDetailsDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
import com.redcurrant.downstream.api.gcash.GcashBalanceApi;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
import com.redcurrant.downstream.dto.gcash.PushRemittanceResponseResponse;
import com.redcurrant.downstream.dto.gcash.ResponseBody;
import com.redcurrant.downstream.dto.gcash.ResultInfo;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponse;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponseResponse;
import com.redcurrant.downstream.dto.gcash.RemittanceStatusResponseResponseBody;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponse;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponseResponse;
import com.redcurrant.downstream.dto.gcash.ValidateAccountResponseResponseBody;
import com.redcurrant.downstream.dto.gcash.BalanceResponse;
import com.redcurrant.downstream.dto.gcash.BalanceResponseBalance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpStatusCodeException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class GCashServiceImplTest {

    @Mock
    private GcashBalanceApi gcashBalanceApi;

    @Mock
    private GcashRemitApi gcashRemitApi;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GCashPropertiesConfig gcashPropertiesConfig;

    @Mock
    private GCashResponseCapture gcashResponseCapture;

    @InjectMocks
    private GCashServiceImpl service;

    @Test
    void sendTxn_success() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(gcashPropertiesConfig.getPublicKey())
            .thenReturn(
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA49PUeCRFXirJLtqYZ5CiXGyZiUsxwQj7jINNsXbClwlnvulGpAEnIKFeN+i01h+jNjTwBdn9uCidu4UWfxUnXVH5+/NJY2Z5iJT1Xi+aEE4cQLTV/u14vhtlwxKDjmqbq8V4C+8Kloa2N67V27SQJ59rzKyb1xTmb+kEHgXqKI0u4+eufIWEwEel24uz1CwbiWxaZp7Lst8Ksevfnf16kIzA2TeBIjZhyK6q0eSSqkN9qUMFJBZITK4VMe6tZzClQMVBsLf7b25n0/CpDlMelLeTd006IZuQtuDXFlGYgeComkuVA5MjKAN8KZJl30H3ym6DJcfII/zEVzKoJRELnQIDAQAB");
        setupGcashResponseCaptureMocks();

        PushRemittanceResponse response = buildPushRemittanceSuccessResponse();
        Mockito.when(gcashRemitApi.pushRemit(any())).thenReturn(response);

        SendTxnResponseDto result = service.sendTxn(request);
        Assertions.assertEquals("2025061111121700010100170113502322434", result.getPartnerRefNo());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_SUCCESS.returnCode, result.getReturnCode());
        Assertions.assertEquals("00000000|SUCCESS|Success", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_SUCCESS.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void sendTxn_failure() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(gcashPropertiesConfig.getPublicKey())
            .thenReturn(
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA49PUeCRFXirJLtqYZ5CiXGyZiUsxwQj7jINNsXbClwlnvulGpAEnIKFeN+i01h+jNjTwBdn9uCidu4UWfxUnXVH5+/NJY2Z5iJT1Xi+aEE4cQLTV/u14vhtlwxKDjmqbq8V4C+8Kloa2N67V27SQJ59rzKyb1xTmb+kEHgXqKI0u4+eufIWEwEel24uz1CwbiWxaZp7Lst8Ksevfnf16kIzA2TeBIjZhyK6q0eSSqkN9qUMFJBZITK4VMe6tZzClQMVBsLf7b25n0/CpDlMelLeTd006IZuQtuDXFlGYgeComkuVA5MjKAN8KZJl30H3ym6DJcfII/zEVzKoJRELnQIDAQAB");
        setupGcashResponseCaptureMocks();

        PushRemittanceResponse response = buildPushRemittanceFailureResponse();
        Mockito.when(gcashRemitApi.pushRemit(any())).thenReturn(response);

        SendTxnResponseDto result = service.sendTxn(request);
        Assertions.assertNull(result.getPartnerRefNo());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.returnCode, result.getReturnCode());
        Assertions.assertEquals("60000000|SYSTEM_ERROR|System related error", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void sendTxn_runtimeException() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"mock\":\"json\"}");
        Mockito.when(gcashRemitApi.pushRemit(any())).thenThrow(new RuntimeException("error"));

        SendTxnResponseDto result = service.sendTxn(request);
        Assertions.assertNull(result.getPartnerRefNo());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.returnCode, result.getReturnCode());
        Assertions.assertEquals("error", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.SEND_TXN_FAILURE.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void enquiryTxn_success() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(gcashPropertiesConfig.getPublicKey())
            .thenReturn(
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA49PUeCRFXirJLtqYZ5CiXGyZiUsxwQj7jINNsXbClwlnvulGpAEnIKFeN+i01h+jNjTwBdn9uCidu4UWfxUnXVH5+/NJY2Z5iJT1Xi+aEE4cQLTV/u14vhtlwxKDjmqbq8V4C+8Kloa2N67V27SQJ59rzKyb1xTmb+kEHgXqKI0u4+eufIWEwEel24uz1CwbiWxaZp7Lst8Ksevfnf16kIzA2TeBIjZhyK6q0eSSqkN9qUMFJBZITK4VMe6tZzClQMVBsLf7b25n0/CpDlMelLeTd006IZuQtuDXFlGYgeComkuVA5MjKAN8KZJl30H3ym6DJcfII/zEVzKoJRELnQIDAQAB");
        setupGcashResponseCaptureMocks();

        RemittanceStatusResponse response = buildTransactionStatusSuccessResponse();
        Mockito.when(gcashRemitApi.getTransactionStatus(any())).thenReturn(response);

        EnquiryResponseDto result = service.enquiryTxn(request);
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_SUCCESS.returnCode, result.getReturnCode());
        Assertions.assertEquals("00000000|SUCCESS|Success", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_SUCCESS.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void enquiryTxn_failure() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(gcashPropertiesConfig.getPublicKey())
            .thenReturn(
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA49PUeCRFXirJLtqYZ5CiXGyZiUsxwQj7jINNsXbClwlnvulGpAEnIKFeN+i01h+jNjTwBdn9uCidu4UWfxUnXVH5+/NJY2Z5iJT1Xi+aEE4cQLTV/u14vhtlwxKDjmqbq8V4C+8Kloa2N67V27SQJ59rzKyb1xTmb+kEHgXqKI0u4+eufIWEwEel24uz1CwbiWxaZp7Lst8Ksevfnf16kIzA2TeBIjZhyK6q0eSSqkN9qUMFJBZITK4VMe6tZzClQMVBsLf7b25n0/CpDlMelLeTd006IZuQtuDXFlGYgeComkuVA5MjKAN8KZJl30H3ym6DJcfII/zEVzKoJRELnQIDAQAB");
        setupGcashResponseCaptureMocks();

        RemittanceStatusResponse response = buildTransactionStatusFailureResponse();
        Mockito.when(gcashRemitApi.getTransactionStatus(any())).thenReturn(response);

        EnquiryResponseDto result = service.enquiryTxn(request);
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE.returnCode, result.getReturnCode());
        Assertions.assertEquals("60000000|SYSTEM_ERROR|System related error", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.ENQUIRY_TXN_FAILURE.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void fetchAcctDtls_success() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(gcashPropertiesConfig.getPublicKey())
            .thenReturn(
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA49PUeCRFXirJLtqYZ5CiXGyZiUsxwQj7jINNsXbClwlnvulGpAEnIKFeN+i01h+jNjTwBdn9uCidu4UWfxUnXVH5+/NJY2Z5iJT1Xi+aEE4cQLTV/u14vhtlwxKDjmqbq8V4C+8Kloa2N67V27SQJ59rzKyb1xTmb+kEHgXqKI0u4+eufIWEwEel24uz1CwbiWxaZp7Lst8Ksevfnf16kIzA2TeBIjZhyK6q0eSSqkN9qUMFJBZITK4VMe6tZzClQMVBsLf7b25n0/CpDlMelLeTd006IZuQtuDXFlGYgeComkuVA5MjKAN8KZJl30H3ym6DJcfII/zEVzKoJRELnQIDAQAB");
        setupGcashResponseCaptureMocks();

        ValidateAccountResponse response = buildValidateAccountSuccessResponse();
        Mockito.when(gcashRemitApi.validateAccount(any())).thenReturn(response);

        AccountDetailsResponseDto result = service.fetchAcctDtls(request);
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_SUCCESS.returnCode, result.getReturnCode());
        Assertions.assertEquals("00000000|SUCCESS|Success", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_SUCCESS.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void fetchAcctDtls_runtimeException() throws JsonProcessingException {
        RequestDto request = buildRcRequestDto();
        mockGcashConfigProperties();
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"mock\":\"json\"}");
        Mockito.when(gcashRemitApi.validateAccount(any())).thenThrow(new RuntimeException("error"));

        AccountDetailsResponseDto result = service.fetchAcctDtls(request);
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE.returnCode, result.getReturnCode());
        Assertions.assertEquals("error", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.ACCOUNT_DETAILS_FAILURE.statusDescription,
                result.getStatusDescription());
    }

    @Test
    void vostroBalEnquiry_success() {
        VostroBalEnquiryRequestDto request = VostroBalEnquiryRequestDto.builder().reqId("1234567").build();
        Mockito.when(gcashPropertiesConfig.getWalletName()).thenReturn("wallet");
        Mockito.when(gcashPropertiesConfig.getWalletPin()).thenReturn("pin");
        BalanceResponse response = new BalanceResponse();
        response.setCode("0");
        response.setMessage("Success");
        BalanceResponseBalance balance = new BalanceResponseBalance();
        balance.setAvailableBalance("7103.00");
        response.setBalance(balance);
        Mockito.when(gcashBalanceApi.getWalletBalance(any())).thenReturn(response);

        VostroBalEnquiryResponseDto result = service.vostroBalEnquiry(request);
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_SUCCESS.returnCode, result.getReturnCode());
        Assertions.assertEquals("0|Success", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_SUCCESS.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_SUCCESS.statusDescription,
                result.getStatusDescription());
        Assertions.assertEquals("7103.00", result.getBalance());
    }

    @Test
    void vostroBalEnquiry_failure() throws JsonProcessingException {
        VostroBalEnquiryRequestDto request = VostroBalEnquiryRequestDto.builder().reqId("1234567").build();
        Mockito.when(gcashPropertiesConfig.getWalletName()).thenReturn("wallet");
        Mockito.when(gcashPropertiesConfig.getWalletPin()).thenReturn("pin");
        HttpStatusCodeException ex = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(ex.getResponseBodyAsString())
            .thenReturn("{\"code\":\"8\",\"message\":\"Provided PIN is incorrect\"}\n");
        BalanceResponse mockBalanceResponse = new BalanceResponse();
        mockBalanceResponse.setCode("8");
        mockBalanceResponse.setMessage("Provided PIN is incorrect");
        Mockito.when(objectMapper.readValue(anyString(), Mockito.eq(BalanceResponse.class)))
            .thenReturn(mockBalanceResponse);
        Mockito.when(gcashBalanceApi.getWalletBalance(any())).thenThrow(ex);

        VostroBalEnquiryResponseDto result = service.vostroBalEnquiry(request);
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals("1234567", result.getReqId());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_FAILURE.returnCode, result.getReturnCode());
        Assertions.assertEquals("8|Provided PIN is incorrect", result.getReturnDescription());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_FAILURE.statusCode, result.getStatusCode());
        Assertions.assertEquals(RcResponseTemplateEnum.GET_BALANCE_FAILURE.statusDescription,
                result.getStatusDescription());
        Assertions.assertNull(result.getBalance());
    }

    private void mockGcashConfigProperties() {
        Mockito.when(gcashPropertiesConfig.getPrivateKey())
            .thenReturn(
                    "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8Z9u/NklMXbygN1Pu9lLtlHvXkry599CRB5r2ETfP4Bgy2rElCd+vYhYXyarK5ViLK1BZ4VdN7vBgfUr9v/yycidplCsXzCwpA7gBBvVvEhcpRjAgam05Pn9uG744Q5p/8uc54IH/Q/t81qjFFSkQemY9HUKJSOigG4dldy7lYAjVNl0094WWvz2hQ/19hdX++YIVfUVGpec9+KT61+GEC7NNbcMNQWvUs4W13OvV1frNtZZAX6/4h9q+dw7yCkT9DGhhlzjD5GR+F12PCljMj5CnbnTgqn58VAzV2xPGF3yPR+mKRdfJ3uLPgHwC7hYKoKZZbTWXyDslfLdcymZnAgMBAAECggEBAIM1bTMwEkHtsfR+M6TixQQR+WE7HtYAgK7P9hpSCWVqsk+QP/gEdVRF4g708k3VBSH+qzm9Fjj/I0Z/W192efyoRUxg1NYA6ewyW83TU8/jcF/dMp0QoTpRjRtYlNPIOOvMdT24XbAxXw4kzGpcfzmrlFIRc4n6TY9bPKLsKquwUMdc6C+IRdj7zE/qQT2uEnsLpqprr7woq5WeFPE4ma4Qx16XLK4gw/c3OHfCwjEpMEqQhi+BvWA5l5yZFJvvY0vS4//p/0f8QQpg3Eqe5x/ioy6fG6MbQCxA1iRUe2mkYKTiszeXFz/aole6VIa8M2sR2vEPONR1FbPCCc32B3kCgYEA4u3Vwa6nSE6KJR7DTFTX5xdST6FmGB+Alw6/Zct6pL7exfk8A01Cvc1fGMW7kIFu0pI0ccrnRZhzLDNOKqSH3Euib9Z9qJc8/RMCDlP/wVRmqhdhs6lqoABIipQBYr5vEhBGYB6KP5Z++DOYebmLINCLWVnKw9wOBlt7zw76jPMCgYEA1IqlSsq/BTxOnt/yAQaUosGt87GuT06THzSDyBT82PAJblEZyTAu80QhVcMCovZsEt6CJ0evQVthcRiUuqfqR9QpHXZ8oMtBomBgUJXWsW9BB7dLWBL1vqO0wgfQ9EOi0Q4YVjb3n3QTNl4dJagXHi028vU5LOJ92NG+0yK4Db0CgYA+bkw2gsRG4kSjPblhEJ5tVz8v5SlfTZtk84u4h6hwForXUGAwUFsiOJQHDq8xbEp+5/ZBsB68SNq2uE6y8JzoAG7p8cqUTTdcMJSKOJXOLFgqR1B7ywoohqtuZJWVAusvvwZ9fyltoQvBYQB7zI34MyeDJ27fP3UkTprbNjW7/QKBgGTvLs4U+KAw4plaep/4ExAz8Dshr0jkZMPV+ZtENEuFusTT6O39Xt6SBU6oHJFteyd+2cpzpYvhLKY76S23XnAcFiZyxX7K5/GH2VWLwvhigDx81GAQKPoO3mGiup21nRWwnugalH/57MHN8gOXDtt613SL9koViXQ1L20vOL71AoGAIvCtEUnsnnwuLIEve93IK4E7ooP2Ko8ShuZWbpUcmg9k7kJ7bjjV690CS6FySeVF9KRPODx74z7eRR79A78eDASjX7Xty33qZmylTg376dYUhMnfDWNDgTW9kdFb2+B097UuZO98aAV4Gb4pdZ+aqzNH/UPH13c8prrALlo9k08=");
        Mockito.when(gcashPropertiesConfig.getKeyAlgorithm()).thenReturn("SHA256withRSA");
        Mockito.when(gcashPropertiesConfig.getClientId()).thenReturn("id");
        Mockito.when(gcashPropertiesConfig.getClientSecret()).thenReturn("secret");
        Mockito.when(gcashPropertiesConfig.getHeadFunction()).thenReturn("fund.remit");
        Mockito.when(gcashPropertiesConfig.getHeadVersion()).thenReturn("1.0.0");
    }

    private void setupGcashResponseCaptureMocks() throws JsonProcessingException {
        Mockito.when(gcashResponseCapture.get()).thenReturn("");

        JsonNode mockRoot = Mockito.mock(JsonNode.class);
        JsonNode mockResponseNode = Mockito.mock(JsonNode.class);
        JsonNode mockSignatureNode = Mockito.mock(JsonNode.class);

        Mockito.when(objectMapper.readTree(anyString())).thenReturn(mockRoot);
        Mockito.when(mockRoot.get("response")).thenReturn(mockResponseNode);
        Mockito.when(mockRoot.get("signature")).thenReturn(mockSignatureNode);

        Mockito.when(objectMapper.writeValueAsString(Mockito.any()))
            .thenReturn(
                    "{\"head\":{\"respTime\":\"2025-06-11T02:28:55+08:00\",\"version\":\"1.0.0\",\"function\":\"fund.remit\",\"clientId\":\"RCCAR00000001\",\"clientSecret\":\"ucarsH20E90WVhRlTkc\",\"reqMsgId\":\"75fe6699-63dd-4121-a8db-1ad0dc892c23\",\"reserve\":{}},\"body\":{\"resultInfo\":{\"resultStatus\":\"S\",\"resultCodeId\":\"00000000\",\"resultCode\":\"SUCCESS\",\"resultMsg\":\"Success\"},\"transactionId\":\"2025061111121700010100170113502322434\",\"action\":\"commit\",\"remcoId\":\"RCCAR00000001\",\"requestId\":\"46pV7YzXaQ9TCLRm6116\",\"gcashAccountCountryCode\":\"63\",\"gcashAccount\":\"9920275946\",\"amount\":{\"currency\":\"PHP\",\"value\":\"151.63\"},\"complianceInfo\":{\"originatingCountry\":\"ARE\",\"senderInfo\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"countryOfBirth\":\"GBR\",\"dateOfBirth\":\"19680516\",\"relationToReceiver\":\"Father\",\"sourceOfIncome\":\"SALARY\",\"idInfo\":{\"idType\":\"NATIONAL_ID\",\"idNumber\":\"89980998-09992873\"}},\"receiverInfo\":{\"firstName\":\"Dito\",\"middleName\":\"IGO\",\"lastName\":\"Cico\"}},\"extendInfo\":{\"storeLocation\":\"AE\",\"refNumber\":\"4pV7YzXaQ9TCLRm6124\",\"cashInRemit\":false}}}");

        Mockito.when(mockSignatureNode.asText())
            .thenReturn(
                    "0ODCqjOVvbRuEass6pZuY1u45C/e/VBBLKaghsZ6234AgCkTbAslRQbLd/gvGV0ZnqE4FDF/0p9MFsGgefWAEl4MDycUE4t19J0LrvMucgSdcL6skmQ5TKaOYOXTQYIhlnMZ7hXAWcu4yrNFCjnCACv3rJC6v1o1ysRjGfJ2CD/XpqK7tmUbV+wcY14V23MbAuC8RHa9cZKQ0XUBFArQup38iFpRpvy7+ATIKT3bzv4OkZLcQBJO2bTj3lJfJwuyAu3yf63cc/wzsDLotyI+L5urx1x/8raTQXkHDrtuhLL7KhyrBpjWDwENfo/vLzhxXCO6mNBRgQP4ucMpm/3C9Q==");

    }

    private RequestDto buildRcRequestDto() {
        MetadataDto.OrderingInstitutionDto orderingInstitutionDto = new MetadataDto.OrderingInstitutionDto();
        MetadataDto.InstitutionAddressDto institutionAddressDto = new MetadataDto.InstitutionAddressDto();
        institutionAddressDto.setCountryCode("AE");
        orderingInstitutionDto.setInstitutionAddress(institutionAddressDto);

        return RequestDto.builder()
            .reqId("1234567")
            .metadata(MetadataDto.builder().orderingInstitution(orderingInstitutionDto).build())
            .sender(SenderDto.builder()
                .senderFirstName("John")
                .senderLastName("Doe")
                .senderPlaceOfBirth("united kingdom")
                .senderBeneRelationship("Father")
                .senderDOB("May 16 1968 00:00:00")
                .id(IdDto.builder().senderIdNo("89980998-09992873").build())
                .build())
            .receiver(ReceiverDto.builder()
                .beneficiaryFirstName("Dito")
                .beneficiaryMiddleName("IGO")
                .beneficiaryLastName("Cico")
                .build())
            .transaction(TransactionDetailsDto.builder()
                .sourceOfIncomeCode("S01")
                .txnRefNo("4pV7YzXaQ9TCLRm6124")
                .agentRefNo("4pV7YzXaQ9TCLRm6124")
                .toReceiver(SenderReceiverDto.builder().amountToSend("151.63").receiveCurrency("PHP").build())
                .optedService(ServiceInfoDto.builder().beneficiaryAccountNumber("+639920275946").build())
                .build())
            .build();

    }

    private PushRemittanceResponse buildPushRemittanceSuccessResponse() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResultStatus("S");
        resultInfo.setResultCodeId("00000000");
        resultInfo.setResultCode("SUCCESS");
        resultInfo.setResultMsg("Success");

        ResponseBody body = new ResponseBody();
        body.setTransactionId("2025061111121700010100170113502322434");
        body.setResultInfo(resultInfo);

        PushRemittanceResponseResponse innerResponse = new PushRemittanceResponseResponse();
        innerResponse.setBody(body);
        PushRemittanceResponse response = new PushRemittanceResponse();
        response.setResponse(innerResponse);
        return response;
    }

    private PushRemittanceResponse buildPushRemittanceFailureResponse() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResultStatus("U");
        resultInfo.setResultCodeId("60000000");
        resultInfo.setResultCode("SYSTEM_ERROR");
        resultInfo.setResultMsg("System related error");

        ResponseBody body = new ResponseBody();
        body.setResultInfo(resultInfo);

        PushRemittanceResponseResponse innerResponse = new PushRemittanceResponseResponse();
        innerResponse.setBody(body);
        PushRemittanceResponse response = new PushRemittanceResponse();
        response.setResponse(innerResponse);
        return response;
    }

    private RemittanceStatusResponse buildTransactionStatusSuccessResponse() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResultStatus("S");
        resultInfo.setResultCodeId("00000000");
        resultInfo.setResultCode("SUCCESS");
        resultInfo.setResultMsg("Success");

        RemittanceStatusResponseResponseBody body = new RemittanceStatusResponseResponseBody();
        body.setResultInfo(resultInfo);

        RemittanceStatusResponseResponse innerResponse = new RemittanceStatusResponseResponse();
        innerResponse.setBody(body);
        RemittanceStatusResponse response = new RemittanceStatusResponse();
        response.setResponse(innerResponse);
        return response;
    }

    private RemittanceStatusResponse buildTransactionStatusFailureResponse() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResultStatus("U");
        resultInfo.setResultCodeId("60000000");
        resultInfo.setResultCode("SYSTEM_ERROR");
        resultInfo.setResultMsg("System related error");

        RemittanceStatusResponseResponseBody body = new RemittanceStatusResponseResponseBody();
        body.setResultInfo(resultInfo);

        RemittanceStatusResponseResponse innerResponse = new RemittanceStatusResponseResponse();
        innerResponse.setBody(body);
        RemittanceStatusResponse response = new RemittanceStatusResponse();
        response.setResponse(innerResponse);
        return response;
    }

    private ValidateAccountResponse buildValidateAccountSuccessResponse() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResultStatus("S");
        resultInfo.setResultCodeId("00000000");
        resultInfo.setResultCode("SUCCESS");
        resultInfo.setResultMsg("Success");

        ValidateAccountResponseResponseBody body = new ValidateAccountResponseResponseBody();
        body.setResultInfo(resultInfo);

        ValidateAccountResponseResponse innerResponse = new ValidateAccountResponseResponse();
        innerResponse.setBody(body);
        ValidateAccountResponse response = new ValidateAccountResponse();
        response.setResponse(innerResponse);
        return response;
    }

}
