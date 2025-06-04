package com.pdd.redcurrant.application.listeners;

import com.pdd.redcurrant.domain.exception.BusinessException;
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

/**
 * This is the **core entry point** of the Redcurrant project.
 * <p>
 * It listens to incoming messages from Solace topics and queues using Spring JMS. Based
 * on whether the communication is asynchronous or synchronous:
 * <ul>
 * <li>{@code handleAsync} consumes and processes fire-and-forget style messages.</li>
 * <li>{@code handleSync} consumes a request and sends a reply message back.</li>
 * </ul>
 * <p>
 * The actual business logic is delegated to {@link SolaceServicePort}, which should be
 * implemented by services using the Redcurrant project.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SolaceListener {

    private final SolaceServicePort solaceService;

    /**
     * Handles asynchronous (fire-and-forget) messages sent to a Solace topic. The message
     * is passed directly to the service layer for processing.
     * @param message The raw JSON payload received from the Solace topic.
     */
    @JmsListener(destination = "${solace.gateway.topic_id}", containerFactory = "topicListenerContainerFactory")
    public void handleAsync(String message) {
        try {
            log.debug("Received message: {}", message);
            solaceService.process(message);
        }
        catch (Exception ex) {
            log.error(MapperUtils.toString(BusinessException.INTERNAL_ERROR.toResponse(ex.getMessage(), null)));
        }
    }

    /**
     * Handles synchronous request-response messages sent to a Solace queue. It processes
     * the request and sends the response to the reply-to destination.
     * @param message The JMS message received from the Solace queue.
     * @param session The JMS session used to create and send the response.
     */
    @JmsListener(destination = "${solace.gateway.queue_name}", containerFactory = "queueListenerContainerFactory")
    public void handleSync(Message message, Session session) {
        if (!(message instanceof TextMessage)) {
            log.warn("Obtained message is not supported: {}, Session {}", message, session);
            return;
        }

        try {
            String json = ((TextMessage) message).getText();
            log.debug("Received message: {}", json);

            String response;
            try {
                response = MapperUtils.toString(solaceService.process(json));
            }
            catch (Exception ex) {
                log.error("Error processing message: {}", ex.getMessage());
                response = MapperUtils.toString(BusinessException.INTERNAL_ERROR.toResponse(ex.getMessage(), null));
            }

            TextMessage responseMessage = session.createTextMessage(response);
            // Set correlation ID from the incoming message
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
            // Send the response back to the temporary reply-to destination
            session.createProducer(message.getJMSReplyTo()).send(responseMessage);
        }
        catch (JMSException jmsEx) {
            log.error("Error with JMS session: {}", jmsEx.getMessage());
            throw new RuntimeException(jmsEx);
        }
    }

}
