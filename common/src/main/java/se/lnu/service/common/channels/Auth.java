package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Auth {
	
	String REGISTER_USER_INPUT = "register-user-in";
	String REGISTER_USER_OUTPUT = "register-user-ex";
	
	String LOGIN_USER_INPUT = "login-user-in";
	String LOGIN_USER_OUTPUT = "login-user-out";
	
	String CHANGE_USER_INPUT = "change-user-in";
	String CHANGE_USER_OUTPUT = "change-user-out";
	
	String GET_USER_BY_EMAIL_INPUT = "get-user-in";
	String GET_USER_BY_EMAIL_OUTPUT = "get-user-out";
	
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
	
	@Input(Auth.GET_USER_BY_EMAIL_INPUT)
	SubscribableChannel getUserByEmailInput();
	
	@Output(Auth.GET_USER_BY_EMAIL_OUTPUT)
	MessageChannel getUserByEmailOutput();
}
