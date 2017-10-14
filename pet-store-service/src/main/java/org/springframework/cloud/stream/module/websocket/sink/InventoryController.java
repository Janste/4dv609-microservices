package org.springframework.cloud.stream.module.websocket.sink;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;

@Controller
@EnableBinding(Inventory.class)
public class InventoryController {
	
	@Autowired
	private Inventory inventory;

	public void addToCart(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			AddToCart addCartRequest = mapper.readValue(json.toString(), AddToCart.class);
			System.out.println(addCartRequest.toString());
			inventory.addToCartOutput().send(MessageBuilder.withPayload(addCartRequest).build());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@StreamListener(Inventory.ADDED_TO_CART_INPUT)
	public void websocketSink(AddToCart message) {
		String messagePayload = message.toString();
		System.out.println("REBOUND: " + messagePayload);
		Set<String> channels = WebsocketSinkServer.emailsToChannel.getOrDefault(message.getUserEmail(), null);
		if (channels != null) {
			for (Channel channel : WebsocketSinkServer.channels) {
				if (channels.contains(channel.id().toString())) {
					channel.write(new TextWebSocketFrame(messagePayload));
					channel.flush();
				}
			}
		}
	}

}
