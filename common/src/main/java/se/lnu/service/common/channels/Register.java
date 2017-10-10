package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Register {
	String INPUT = "register-in";
	String OUTPUT = "register-ex";
	
	@Input(Register.INPUT)
	SubscribableChannel input();
	
	@Output(Register.OUTPUT)
	MessageChannel output();
}
