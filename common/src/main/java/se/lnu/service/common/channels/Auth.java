package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Auth {
	
	String REGISTER_USER_INPUT = "register-user-in";
	String REGISTER_USER_OUTPUT = "register-user-ex";
	
	String LOGIN_USER_INPUT = "login-user-ex";
	String LOGIN_USER_OUTPUT = "login-user-ex";
	
	String CHANGE_USER_INPUT = "change-user-ex";
	String CHANGE_USER_OUTPUT = "change-user-ex";
	
	String GET_USER_BY_EMAIL_INPUT = "get-user-email-ex";
	String GET_USER_BY_EMAIL_OUTPUT = "get-user-email-ex";
	
	@Input(Auth.REGISTER_USER_INPUT)
	SubscribableChannel registerUserInput();
	
	@Output(Auth.REGISTER_USER_OUTPUT)
	MessageChannel registerUserOutput();
	
	@Input(Auth.LOGIN_USER_INPUT)
	SubscribableChannel loginUserInput();
	
	@Output(Auth.LOGIN_USER_OUTPUT)
	MessageChannel loginUserOutput();
	
	@Input(Auth.CHANGE_USER_INPUT)
	SubscribableChannel changeUserInput();
	
	@Output(Auth.CHANGE_USER_OUTPUT)
	MessageChannel changeUserOutput();
	
	@Output(Auth.GET_USER_BY_EMAIL_INPUT)
	MessageChannel getUserByEmailInput();
	
	@Output(Auth.GET_USER_BY_EMAIL_OUTPUT)
	MessageChannel getUserByEmailOutput();
}
