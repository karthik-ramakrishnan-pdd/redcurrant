package com.pdd.redcurrant.application.listeners;

import com.pdd.redcurrant.domain.ports.api.SolaceServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolaceListener {

    private final SolaceServicePort solaceService;

    @Value("${solace.queue.test.sync}")
    private String syncQueueName;

    @JmsListener(destination = "${solace.topic.test.async}", containerFactory = "topicListenerContainerFactory")
    public void handleAsync(String message) {
        try {
            log.info("Received: {}", message);
            solaceService.process(message);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @JmsListener(destination = "${solace.queue.test.sync}", containerFactory = "queueListenerContainerFactory")
    public void handleSync(Message message, Session session) {
        log.info("Listener invoked for queue: {}", syncQueueName);
        if (! (message instanceof TextMessage)) {
            log.warn("Obtained message is not supported: {}", message);
            return;
        }

        try {
            String json = ((TextMessage) message).getText();
            log.info("Received message: {}", json);

            String response;
            try {
                response = MapperUtils.toString(solaceService.processAndReturn(json));
            } catch (Exception ex) {
                log.error("Error processing message: {}", ex.getMessage());
                response = "Error Processing Data";
            }

            TextMessage responseMessage = session.createTextMessage(response);
            // Set correlation ID from the incoming message
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
            // Send the response back to the temporary reply-to destination
            session.createProducer(message.getJMSReplyTo()).send(responseMessage);
        } catch (JMSException jmsEx) {
            log.error("Error with JMS session: {}", jmsEx.getMessage());
        }
    }

}
