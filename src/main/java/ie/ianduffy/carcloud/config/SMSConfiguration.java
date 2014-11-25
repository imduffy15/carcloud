package ie.ianduffy.carcloud.config;

import com.nexmo.messaging.sdk.NexmoSmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SMSConfiguration implements EnvironmentAware {


    private static final String ENV_NEXOMO = "nexmo.";
    private static final String PROP_KEY = "key";
    private static final String PROP_SECRET = "secret";
    private final Logger log = LoggerFactory.getLogger(SMSConfiguration.class);
    private RelaxedPropertyResolver propertyResolver;

    @Bean
    public NexmoSmsClient nexmoSmsClient() {
        log.debug("Configuring SMS Client");
        String key = propertyResolver.getProperty(PROP_KEY);
        String secret = propertyResolver.getProperty(PROP_SECRET);
        NexmoSmsClient nexmoSmsClient = null;
        try {
            nexmoSmsClient = new NexmoSmsClient(key, secret);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return nexmoSmsClient;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_NEXOMO);
    }
}
