package se.lnu.services.user;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.User;

@SpringBootApplication
@EnableBinding(Auth.class)
public class Application {

	
	protected Logger logger = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@StreamListener(Auth.REGISTER_USER_INPUT)
	public void registerUser(User user) {
		logger.info("Registering user: " + user.toString());
		
		
	}
	
	@StreamListener(Auth.LOGIN_USER_INPUT)
	public void loginUser(User user) {
		logger.info("Logging in user: " + user.toString());
	}
	
	@StreamListener(Auth.CHANGE_USER_INPUT)
	public void changeUser(User user) {
		logger.info("Changing user: " + user.toString());
	}
	
	@StreamListener(Auth.GET_USER_BY_EMAIL_INPUT)
	public void getserByEmail(User user) {
		logger.info("Getting user: " + user.toString());
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
