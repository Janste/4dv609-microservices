package org.springframework.cloud.stream.module.websocket.sink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.User;

@Controller
@EnableBinding(Auth.class)
public class UserController {
	
	@Autowired
	private Auth auth;
	
    public void registerUser(User user) {
		System.out.println(user.toString());
		auth.output().send(MessageBuilder.withPayload(user).build());
    }

}
