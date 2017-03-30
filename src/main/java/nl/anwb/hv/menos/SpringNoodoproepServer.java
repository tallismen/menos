package nl.anwb.hv.menos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Properties;

@SpringBootApplication
public class SpringNoodoproepServer extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SpringNoodoproepServer.class);

    @Autowired
    private Environment env;

    /**
     * De Main methode van de applicatie
     */
    public static void main(String[] args) {
        logger.info("Starten Applicatie");
        new SpringApplicationBuilder(SpringNoodoproepServer.class)
                .sources(SpringNoodoproepServer.class)
                .properties(getProperties())
                .run(args);    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.info("Configure Applicatie");
        return application.sources(SpringNoodoproepServer.class).properties(getProperties());
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        String configLocation = System.getProperty("config.dir", "conf/LOC/");
        if (!configLocation.endsWith("/")) {
            configLocation += "/";
        }
        props.setProperty("spring.config.location", configLocation);
        props.setProperty("logging.config", configLocation + "logback.xml");

        logger.info("spring.config.location={}", configLocation);
        return props;
    }

    @Bean
    public MessageConverter messageConverter() {
        logger.info("messageConverter()");
        return new OxiMessageConverter();
    }

    /**
     * Deze methode maakt een ConnectionFactory die de standaart overschrijft.
     * Daarnaast staat hier de instellingen van de ConnectionFactory.
     */
    @Bean
    public ConnectionFactory connectionFactory() throws JMSException {
        logger.info("connectionFactory()");
        progress.message.jclient.ConnectionFactory cf = new progress.message.jclient.ConnectionFactory();
        cf.setBrokerURL(env.getProperty("jms.broker"));
        cf.setDefaultUser(env.getProperty("jms.user"));
        cf.setDefaultPassword(env.getProperty("jms.passwd"));
        cf.setFaultTolerant(true);
        cf.setFaultTolerantReconnectTimeout(0);
        cf.setInitialConnectTimeout(90);
        cf.setSocketConnectTimeout(5000);
        logger.info(cf.toString());
        return new CachingConnectionFactory(cf);
    }
}
