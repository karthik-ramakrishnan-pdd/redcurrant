package com.pdd.redcurrant.application.listeners;

import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolaceListener {

    private final SolaceServicePort solaceService;

    @JmsListener(destination = "${solace.topic.test.async}")
    public void handleAsync(String message) {
        try {
            log.info("Received: {}", message);
            solaceService.process(message);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @JmsListener(destination = "${solace.topic.test.sync}")
    public void handleSync(Message message, Session session) {
        try {
            if (message instanceof TextMessage textMessage) {
                String json = textMessage.getText();
                log.info("Received message: {}", json);

                // Create a response message
                var responseMessage = session
                    .createTextMessage(MapperUtils.toString(solaceService.processAndReturn(json)));
                // Set correlation ID from the incoming message
                responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());

                // Send the response back to the temporary reply-to destination
                session.createProducer(message.getJMSReplyTo()).send(responseMessage);
            }
        }
        catch (JMSException ex) {
            log.error("Error processing message: {}", ex.getMessage(), ex);
        }
    }

}
