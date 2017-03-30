package nl.anwb.hv.menos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * Deze class is de JmsClient van deze applicatie, nodig voor spring.
 */
@Service
public class JmsClient {

    private static final Logger logger = LoggerFactory.getLogger(JmsClient.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * De destination queue waarnaar berichten worden verstuurd
     * en er worden nu nog tijdelijk berichten van gelezen
     */
    @Value("${jms.queue.destination}")
    private String destinationQueue;

    @Value("${jms.queue.reply}")
    private String replyQueue;

    private Destination replyDestination;
    private Message sentMessage;

    /**
     * Verstuurd een bericht naar de destination.
     *
     * @param msg Het bericht
     */
    public Object send(String msg) throws JMSException {
        logger.info("Convert and Send JMS MSG");

        jmsTemplate.convertAndSend(destinationQueue, msg, new MessagePostProcessor() {
            public Message postProcessMessage(Message message) throws JMSException {
                message.setJMSReplyTo(getReplyDestination());
                sentMessage = message;
                return message;
            }
        });

        String msgId = sentMessage.getJMSMessageID();
        Object replay = jmsTemplate.receiveSelected(getReplyDestination(), "JMSCorrelationID = '" + msgId + "'");
        return replay;
    }

    /**
     * Ontvangt berichten van de destinationQueue
     *
     * @return Het ontvangen bericht.
     */
    public String receive() {
        logger.info("Recieve and convert JMS MSG");
        return jmsTemplate.receiveAndConvert(destinationQueue).toString();
    }

    private Destination getReplyDestination() {
        if (replyDestination == null) {
            replyDestination = jmsTemplate.execute(new SessionCallback<Destination>() {
                public Destination doInJms(Session session) throws JMSException {
                    Queue queue = session.createTemporaryQueue(); //session.createQueue(replyQueue);
                    return queue;
                }
            }, false);
        }
        return replyDestination;
    }
}
