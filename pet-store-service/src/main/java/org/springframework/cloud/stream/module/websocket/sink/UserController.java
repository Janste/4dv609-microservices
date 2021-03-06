package org.springframework.cloud.stream.module.websocket.sink;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.RequestUser;
import se.lnu.service.common.message.User;

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
    
    @StreamListener(Auth.REGISTER_USER_INPUT)
    public void registerUserSink(RequestUser user) {
    	String target = user.getChannelId();
		for (Channel channel : WebsocketSinkServer.channels) {
			if (channel.id().toString().equals(target)) {
				channel.write(new TextWebSocketFrame(user.toString()));
				channel.flush();
			}
		}
    }

    public void loginUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.toString(), RequestUser.class);
			auth.loginUserOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @StreamListener(Auth.LOGIN_USER_INPUT)
    public void loginUserSink(RequestUser user) {
    	String target = user.getChannelId();
		for (Channel channel : WebsocketSinkServer.channels) {
			if (channel.id().toString().equals(target)) {
				channel.write(new TextWebSocketFrame(user.toString()));
				channel.flush();
			}
		}
    }
    
    public void changeUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.toString(), RequestUser.class);
			auth.changeUserOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @StreamListener(Auth.CHANGE_USER_INPUT)
    public void changeUserSink(RequestUser user) {
    	Set<String> channels = WebsocketSinkServer.emailsToChannel.getOrDefault(user.getUser().getEmail(), null);
		if (channels != null) {
			for (Channel channel : WebsocketSinkServer.channels) {
				if (channels.contains(channel.id().toString())) {
					channel.write(new TextWebSocketFrame(user.toString()));
					channel.flush();
				}
			}
		}
    }
    
    public void getUser(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RequestUser request = mapper.readValue(json.toString(), RequestUser.class);
			User user = new User();
			user.setEmail(json.get("userEmail").getAsString());
			request.setUser(user);
			auth.getUserByEmailOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @StreamListener(Auth.GET_USER_BY_EMAIL_INPUT)
    public void getUserSink(RequestUser user) {
    	Set<String> channels = WebsocketSinkServer.emailsToChannel.getOrDefault(user.getUser().getEmail(), null);
		if (channels != null) {
			for (Channel channel : WebsocketSinkServer.channels) {
				if (channels.contains(channel.id().toString())) {
					channel.write(new TextWebSocketFrame(user.toString()));
					channel.flush();
				}
			}
		}
    }
}
