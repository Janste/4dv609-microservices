package se.lnu.services.user;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.RequestUser;
import se.lnu.service.common.message.User;

@SpringBootApplication
@EnableBinding(Auth.class)
public class Application {

	private UserRepository repository = new UserRepository();
	protected Logger logger = Logger.getLogger(Application.class.getName());

	@Autowired
	private Auth auth;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@StreamListener(Auth.REGISTER_USER_INPUT)
	@SendTo(Auth.REGISTER_USER_OUTPUT)
	public RequestUser registerUser(RequestUser request) {
		logger.info("Registering user: " + request.getUser().toString());

		User user = repository.registerUser(request.getUser());	
		if (user != null) {
			User out = new User();
			out.setToken(user.getToken());
			out.setEmail(user.getEmail());
			request.setSuccess(true);
			request.setUser(out);
		} else {
			request.setSuccess(false);
			request.setError("Could not register user");
		}
		return request;
	}
	
	@StreamListener(Auth.LOGIN_USER_INPUT)
	@SendTo(Auth.LOGIN_USER_OUTPUT)
	public RequestUser loginUser(RequestUser request) {
		logger.info("Logging in user: " + request.getUser().toString());
		
		User user = repository.loginUser(request.getUser().getEmail(), request.getUser().getPassword());
		
		if (user != null) {
			request.setSuccess(true);
			request.setUser(user);
		} else {
			request.setSuccess(false);
		}
		return request;
	}
	
	@StreamListener(Auth.CHANGE_USER_INPUT)
	@SendTo(Auth.CHANGE_USER_OUTPUT)
	public RequestUser changeUser(RequestUser request) {
		logger.info("Changing user: " + request.getUser().toString());
		
		if (repository.changeUser(request.getUser())) {
			request.setSuccess(true);
		} else {
			request.setSuccess(false);
		}
		return request;
	}
	
	@StreamListener(Auth.GET_USER_BY_EMAIL_INPUT)
	@SendTo(Auth.GET_USER_BY_EMAIL_OUTPUT)
	public RequestUser getserByEmail(RequestUser request) {
		logger.info("Getting user: " + request.getUser().toString());
		
		User user = repository.getUserByEmail(request.getUser().getEmail());
		
		if (user != null) {
			request.setSuccess(true);
			request.setUser(user);
		} else {
			request.setSuccess(false);
		}
		return request;
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
