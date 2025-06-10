package com.pdd.redcurrant.application.controller.internal;

import com.pdd.redcurrant.application.annotations.ExcludeFromJacocoGeneratedReport;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.jms.JMSException;
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

    private final JmsTemplate jmsTemplate;

    @Value("${solace.gateway.sync.name}")
    private String queueName;

    @Value("${solace.gateway.async.topic}")
    private String topicId;

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
    public void testAsync(@RequestBody String request) {
        try {
            MapperUtils.isValid(request);
            jmsTemplate.convertAndSend(topicId, request);
            log.info("Published: {}", request);
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
    public String testSync(@RequestBody String request) {
        try {
            MapperUtils.isValid(request);
            log.info("Publishing message: {}", request);

            // Send the message and wait for a response
            Message responseMessage = jmsTemplate.sendAndReceive(queueName, (Session session) -> {
                TextMessage textMessage = session.createTextMessage(request);
                textMessage.setJMSCorrelationID(UUID.randomUUID().toString());
                return textMessage;
            });

            log.info("Received response for publishing message: {}", request);

            if (responseMessage instanceof TextMessage textMessage) {
                return textMessage.getText();
            }
            else {
                log.warn("No response received for message: {}", request);
                return null;
            }
        }
        catch (JmsException | JMSException ex) {
            log.error("Failed to publish sync message: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
