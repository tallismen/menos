package nl.anwb.hv.menos;

import org.anwb.hv.oxi3.util.Oxi3JaxbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.StringUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * De OxiMessageConverter convert berichten naar XML
 */
public class OxiMessageConverter implements MessageConverter {

    private static final String PROP_PROCESS = "PROCES";
    private static final String PROP_SENDER = "SENDER";

    private static final Logger logger = LoggerFactory.getLogger(OxiMessageConverter.class);

    @Value("jms.proces")
    private String _process;
    @Value("jms.app")
    private String _sender;

    private MessageConverter jmsConverter = new SimpleMessageConverter();

    public OxiMessageConverter() {
        logger.info("Initialiseren OxiMessageConverter...");
    }

    /**
     * Deze methode marshallt het object naar XML
     */
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        logger.info("toMessage()");
        String textMsg;
        if (object instanceof String) {
            textMsg = (String) object;
        }
        else {
            try {
                textMsg = Oxi3JaxbContext.marshall(object, true);
            } catch (JAXBException e) {
                throw new MessageConversionException(e.getMessage(), e);
            } catch (IOException e) {
                throw new MessageConversionException(e.getMessage(), e);
            }
        }
        Message msg = jmsConverter.toMessage(textMsg, session);

        if (!StringUtils.isEmpty(_process)) {
            msg.setStringProperty(PROP_PROCESS, _process);
        }
        if (!StringUtils.isEmpty(_sender)) {
            msg.setStringProperty(PROP_SENDER, _sender);
        }
        logger.info(msg.toString());
        return msg;
    }

    /**
     * Deze methode haalt een message uit XML
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        logger.info("fromMessage()");
        Object decoded = jmsConverter.fromMessage(message);

        String body = null;
        if (decoded instanceof String) {
            body = (String) decoded;
        }
        else {
            logger.warn("Unexpected message type: {}", decoded.getClass());
        }

        Object result = body;
        try {
            result = Oxi3JaxbContext.unmarshall(body);
        }
        catch (Exception e) {
            logger.warn("Exception while decoding message: {}", e.getMessage());
            throw new MessageConversionException(e.getMessage(), e);
        }
        logger.info(result.toString());
        return result;
    }
}

