package com.pdd.redcurrant.application.controller.internal;

//import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.application.annotations.ExcludeFromJacocoGeneratedReport;
import com.pdd.redcurrant.domain.configuration.GcashPropertiesConfig;
import com.pdd.redcurrant.domain.configuration.GcashResponseCapture;
import com.pdd.redcurrant.domain.data.ResponseDto;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.request.VostroBalEnquiryRequestDto;
//import com.pdd.redcurrant.domain.data.request.common.ReceiverDto;
//import com.pdd.redcurrant.domain.data.request.common.IdDto;
//import com.pdd.redcurrant.domain.data.request.common.SenderDto;
//import com.pdd.redcurrant.domain.data.request.common.SenderReceiverDto;
//import com.pdd.redcurrant.domain.data.request.common.TransactionDetailsDto;
//import com.pdd.redcurrant.domain.data.request.common.MetadataDto;
//import com.pdd.redcurrant.domain.data.request.common.ServiceInfoDto;
import com.pdd.redcurrant.domain.data.response.AccountDetailsResponseDto;
import com.pdd.redcurrant.domain.data.response.EnquiryResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.VostroBalEnquiryResponseDto;
//import com.redcurrant.downstream.dto.gcash.SenderInfo;
//import com.redcurrant.downstream.dto.gcash.PushRemittanceRequest;
//import com.redcurrant.downstream.dto.gcash.PushRemittanceRequestRequest;
//import com.redcurrant.downstream.dto.gcash.PushRemittanceResponse;
//import com.redcurrant.downstream.dto.gcash.Amount;
//import com.redcurrant.downstream.dto.gcash.ComplianceInfo;
//import com.redcurrant.downstream.dto.gcash.ExtendInfo;
//import com.redcurrant.downstream.dto.gcash.IdInfo;
//import com.redcurrant.downstream.dto.gcash.ReceiverInfo;
//import com.redcurrant.downstream.dto.gcash.RequestHead;
import com.pdd.redcurrant.domain.ports.api.GcashServicePort;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
//import com.pdd.redcurrant.domain.utils.RsaCryptoUtils;
import com.redcurrant.downstream.api.gcash.GcashRemitApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.time.OffsetDateTime;
//import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This controller is intended **strictly for internal testing and validation** purposes
 * within the Redcurrant project.
 * <p>
 * It contains mock integrations for: - Executing stored procedures directly - Sending
 * messages over Solace (sync/async)
 * <p>
 * ⚠️ **Note:** These are mock implementations to assist development and testing. Actual
 * logic should be implemented in services that consume the Redcurrant library.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1/internal")
@Tag(name = "Internal Endpoints")
@ExcludeFromJacocoGeneratedReport
@Slf4j
public class InternalEndpointController {

    private final StoredProcedureServicePort storedProcedureService;

    private final GcashServicePort gcashServiceImpl;

    private final GcashRemitApi gcashRemitApi;

    private final ObjectMapper objectMapper;

    private final GcashPropertiesConfig gcashPropertiesConfig;

    private final JmsTemplate jmsTemplate;

    private final GcashResponseCapture responseCapture;

    @Value("${solace.gateway.queue_name}")
    private String queueName;

    @Value("${solace.gateway.topic_id}")
    private String topicId;

    @GetMapping("/test-balance")
    public VostroBalEnquiryResponseDto triggerGcashBalance() {
        return gcashServiceImpl.vostroBalEnquiry(new VostroBalEnquiryRequestDto());
    }

    @PostMapping("/test-remit")
    public SendTxnResponseDto triggerGcashRemit(@RequestBody RequestDto request) {
        return gcashServiceImpl.sendTxn(request);
    }

    @PostMapping("/test-inquiry")
    public EnquiryResponseDto triggerGcashInquiry(@RequestBody RequestDto request) {
        return gcashServiceImpl.enquiryTxn(request);
    }

    @PostMapping("/test-validate")
    public AccountDetailsResponseDto triggerGcashValidate(@RequestBody RequestDto request) {
        return gcashServiceImpl.fetchAcctDtls(request);
    }

    //
    // public PushRemittanceResponse submitRemittance() {
    // try {
    // PushRemittanceRequestRequest requestPayload = buildRealRemitPayload();
    // String jsonToSign = objectMapper.writeValueAsString(requestPayload);
    // log.info("🔐 JSON to sign:\n{}", jsonToSign);
    //
    // PrivateKey privateKey =
    // RsaCryptoUtils.loadPrivateKey(gcashPropertiesConfig.getPrivateKey());
    // String signature = RsaCryptoUtils.sign(jsonToSign, privateKey,
    // gcashPropertiesConfig.getKeyAlgorithm());
    //
    // PushRemittanceRequest fullRequest = new PushRemittanceRequest();
    // fullRequest.setRequest(requestPayload);
    // fullRequest.setSignature(signature);
    //
    // PushRemittanceResponse response = gcashRemitApi.pushRemit(fullRequest);
    //
    // String rawJson = responseCapture.get();
    // JsonNode root = objectMapper.readTree(rawJson);
    // JsonNode responseNode = root.get("response");
    // String responseJson = objectMapper.writeValueAsString(responseNode);
    // String partnerSignature = root.get("signature").asText();
    //
    // log.info("🔍 JSON to verify:\n{}", responseJson);
    //
    // PublicKey partnerPublicKey =
    // RsaCryptoUtils.loadPublicKey(gcashPropertiesConfig.getPublicKey());
    // boolean verified = RsaCryptoUtils.verify(responseJson, partnerSignature,
    // partnerPublicKey,
    // gcashPropertiesConfig.getKeyAlgorithm());
    //
    // if (!verified) {
    // log.info("❌ Signature verification failed – response may be tampered.");
    // }
    // else {
    // log.info("✅ Signature verified successfully.");
    // }
    // return response;
    //
    // }
    // catch (Exception ex) {
    // log.error("❌ Remittance call failed", ex);
    // throw new RuntimeException("Remittance failed", ex);
    // }
    // }
    //
    // private PushRemittanceRequestRequest buildRealRemitPayload() {
    // // head
    // RequestHead head = new RequestHead();
    // head.setVersion("1.0.0");
    // head.setFunction("fund.remit");
    // head.clientId("RCCAR00000001");
    // head.clientSecret("ucarsH20E90WVhRlTkc");
    // head.reqTime(OffsetDateTime.parse("2025-05-13T13:01:51+02:00").toInstant());
    // head.reqMsgId("08835c4c-6f56-4fc3-9373-fef610");
    // head.setReserve(new HashMap<>()); // reserve is empty
    //
    // // sender
    // IdInfo idInfo = new IdInfo();
    // idInfo.setIdType(IdInfo.IdTypeEnum.NATIONAL_ID);
    // idInfo.setIdNumber("89980998-09992873");
    //
    // SenderInfo sender = new SenderInfo();
    // sender.setFirstName("John");
    // sender.setLastName("Doe");
    // sender.setCountryOfBirth("USA");
    // sender.setDateOfBirth("20000901");
    // sender.setRelationToReceiver("Father");
    // sender.setSourceOfIncome("Farmer");
    // sender.setIdInfo(idInfo);
    //
    // // receiver
    // ReceiverInfo receiver = new ReceiverInfo();
    // receiver.setFirstName("BILLSPAY");
    // receiver.setLastName("UAT TESTING");
    //
    // // compliance
    // ComplianceInfo compliance = new ComplianceInfo();
    // compliance.setOriginatingCountry("USA");
    // compliance.setSenderInfo(sender);
    // compliance.setReceiverInfo(receiver);
    //
    // // amount
    // Amount amount = new Amount();
    // amount.setCurrency("PHP");
    // amount.setValue("100.00");
    //
    // // extend info
    // ExtendInfo extendInfo = new ExtendInfo();
    // extendInfo.setStoreLocation("Dubai");
    // extendInfo.setRefNumber("GCASH8998099809992873");
    //
    // // body
    // com.redcurrant.downstream.dto.gcash.RequestBody body = new
    // com.redcurrant.downstream.dto.gcash.RequestBody();
    // body.setAction(com.redcurrant.downstream.dto.gcash.RequestBody.ActionEnum.COMMIT);
    // body.setRemcoId("RCCAR00000001");
    // body.requestId(java.util.UUID.randomUUID().toString());
    // body.setGcashAccountCountryCode("63");
    // body.setGcashAccount("9056628913");
    // body.setComplianceInfo(compliance);
    // body.setExtendInfo(extendInfo);
    // body.setAmount(amount);
    //
    // PushRemittanceRequestRequest request = new PushRemittanceRequestRequest();
    // request.setHead(head);
    // request.setBody(body);
    // return request;
    // }

    /**
     * Fetches the result of a stored procedure with optional parameters.
     * @param name The name of the stored procedure to execute.
     * @param params Optional list of parameters for the procedure.
     * @return The stringified result of the stored procedure.
     */
    @GetMapping(path = "procedure/{name}")
    public String fetch(@PathVariable(name = "name") String name,
            @RequestParam(name = "params", required = false) List<String> params) {
        return storedProcedureService.fetch(name, (params != null) ? params.toArray() : new Object[0]);
    }

    /**
     * Sends a mock message asynchronously over Solace for internal testing.
     * @param request The mock request payload to send.
     */
    @PostMapping(path = "solace/async")
    public void testAsync(@RequestBody RequestDto request) {
        try {
            String message = MapperUtils.toString(request);
            jmsTemplate.convertAndSend(topicId, message);
            log.info("Published: {}", message);
        }
        catch (Exception ex) {
            log.error("Failed to publish async message: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sends a mock message synchronously over Solace and waits for a response.
     * @param request The mock request payload to send.
     * @return The response DTO received from the Solace queue (or null if none).
     */
    @PostMapping(path = "solace/sync")
    public ResponseDto testSync(@RequestBody RequestDto request) {
        try {
            String message = MapperUtils.toString(request);
            log.info("Publishing message: {}", message);

            // Send the message and wait for a response
            Message responseMessage = jmsTemplate.sendAndReceive(queueName, (Session session) -> {
                TextMessage textMessage = session.createTextMessage(message);
                textMessage.setJMSCorrelationID(UUID.randomUUID().toString());
                return textMessage;
            });

            log.info("Received response for publishing message: {}", message);

            if (responseMessage instanceof TextMessage textMessage) {
                return MapperUtils.convert(textMessage, ResponseDto.class);
            }
            else {
                log.warn("No response received for message: {}", message);
                return null;
            }
        }
        catch (JmsException ex) {
            log.error("Failed to publish sync message: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
