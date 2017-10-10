package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Auth {
	String REGISTER_INPUT = "register-in";
	String REGISTER_OUTPUT = "register-ex";
	
	@Input(Auth.REGISTER_INPUT)
	SubscribableChannel input();
	
	@Output(Auth.REGISTER_OUTPUT)
	MessageChannel output();
}
