package org.springframework.cloud.stream.module.websocket.sink;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.RequestUser;

@Controller
@EnableBinding(Auth.class)
public class UserController {
	
	@Autowired
	private Auth auth;
	
    public void registerUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.toString(), RequestUser.class);
			auth.registerUserOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void loginUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.getAsJsonObject("user").toString(), RequestUser.class);
			auth.loginUserOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void changeUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.getAsJsonObject("user").toString(), RequestUser.class);
			auth.changeUserOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void getUserByEmail(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.getAsJsonObject("user").toString(), RequestUser.class);
			auth.getUserByEmailOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
