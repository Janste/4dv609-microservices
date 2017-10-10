package se.lnu.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.User;

@EnableBinding(Auth.class)
@RestController
public class AuthenticationController {

	@Autowired
	private Auth auth;
	
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String registerUser(@RequestBody User payload) {
		System.out.println(payload.toString());
		auth.output().send(MessageBuilder.withPayload(payload).build());
		return "success";
	}
}
