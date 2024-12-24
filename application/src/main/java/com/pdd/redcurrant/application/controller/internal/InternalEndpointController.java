package com.pdd.redcurrant.application.controller.internal;

import com.pdd.redcurrant.application.annotations.ExcludeFromJacocoGeneratedReport;
import com.pdd.redcurrant.domain.data.MockDto;
import com.pdd.redcurrant.domain.ports.api.StoredProcedureServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1/internal")
@Tag(name = "Internal Endpoints")
@ExcludeFromJacocoGeneratedReport
@Slf4j
public class InternalEndpointController {

    private final StoredProcedureServicePort storedProcedureService;

    private final JmsTemplate jmsTemplate;

    @Value("${solace.topic.test.sync}")
    private String topicSync;

    @Value("${solace.topic.test.async}")
    private String topicAsync;

    @GetMapping(path = "procedure/{name}")
    public String fetch(@PathVariable(name = "name") String name,
                        @RequestParam(name = "params", required = false) List<String> params) {
        return storedProcedureService.fetch(name, params.toArray());
    }

    @PostMapping(path = "solace/async")
    public void testAsync(@RequestBody MockDto request) {
        String message = MapperUtils.toString(request);
        jmsTemplate.convertAndSend(topicAsync, message);
        log.info("Published: {}", message);
    }

    @PostMapping(path = "solace/sync")
    public MockDto testSync(@RequestBody MockDto request) {
        String message = MapperUtils.toString(request);
        log.info("Publishing message: {}", message);

        // Send the message and wait for the response
        Message responseMessage = jmsTemplate.sendAndReceive(topicSync, (Session session) -> {
            TextMessage textMessage = session.createTextMessage(message);
            // Set preferred correlation ID
            textMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            return textMessage;
        });
        log.info("Received response for publishing message: {}", message);

        // Check if the response message is not null and return its body
        if (responseMessage instanceof TextMessage textMessage) {
            return MapperUtils.convert(textMessage, MockDto.class);
        } else {
            log.warn("No response received for message: {}", message);
            return null; // Handle null case appropriately
        }
    }

}