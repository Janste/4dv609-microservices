package se.lnu.services.auth;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.User;


@SpringBootApplication
@EnableBinding(Auth.class)
public class Application {

	protected Logger logger = Logger.getLogger(Application.class.getName());
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@InboundChannelAdapter(value = Auth.REGISTER_OUTPUT, poller = @Poller(fixedDelay = "10000", maxMessagesPerPoll = "1"))
	public MessageSource<User> userSource() {
		return () -> {
			User u = new User();
			u.setFirstName("TestName");
			logger.info("Sending");
			return new GenericMessage<>(u); 
		};
	}
	
	@Bean
	public AlwaysSampler defaultSampler() {
	  return new AlwaysSampler();
	}
	
}
