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

import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;
import se.lnu.service.common.message.CompleteOrderRequest;
import se.lnu.service.common.message.RemoveFromCart;
import se.lnu.service.common.message.RequestCart;
import se.lnu.service.common.message.RequestPets;

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
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void removeFromCart(JsonObject json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RemoveFromCart request = mapper.readValue(json.toString(), RemoveFromCart.class);
			System.out.println(request.toString());
			inventory.removeCartOutput().send(MessageBuilder.withPayload(request).build());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void requestInventory(String connectionId) {
		RequestPets pets = new RequestPets();
		pets.setConnectionID(connectionId);
		inventory.requestInventoryOutput().send(MessageBuilder.withPayload(pets).build());
	}
	
	public void requestCart(String email) {
		RequestCart cart = new RequestCart();
		cart.setUserId(email);
		inventory.requestCartOutput().send(MessageBuilder.withPayload(cart).build());
	}
	
	public void submitOrder(String email) {
		CompleteOrderRequest request = new CompleteOrderRequest();
		request.setUserEmail(email);
		inventory.completeRequestOutput().send(MessageBuilder.withPayload(request).build());
	}
	
	@StreamListener(Inventory.REQUEST_CART_INPUT)
	public void requestCartInput(RequestCart cart) {
		Set<String> channels = WebsocketSinkServer.emailsToChannel.getOrDefault(cart.getUserEmail(), null);
		if (channels != null) {
			for (Channel channel : WebsocketSinkServer.channels) {
				if (channels.contains(channel.id().toString())) {
					channel.write(new TextWebSocketFrame(cart.toString()));
					channel.flush();
				}
			}
		}
	}
	
	@StreamListener(Inventory.REQUEST_INVENTORY_INPUT)
	public void requestInventoryInput(RequestPets pets) {
		String target = pets.getConnectionID();
		for (Channel channel : WebsocketSinkServer.channels) {
			if (channel.id().toString().equals(target)) {
				channel.write(new TextWebSocketFrame(pets.toString()));
				channel.flush();
			}
		}
	}
	
	@StreamListener(Inventory.ADDED_TO_CART_INPUT)
	public void websocketSink(AddToCart message) {
		String messagePayload = message.toString();
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
	
	@StreamListener(Inventory.REMOVE_CART_INPUT)
	public void websocketSink(RemoveFromCart message) {
		String messagePayload = message.toString();
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
	
	@StreamListener(Inventory.COMPLETE_REQUEST_INPUT)
	public void websocketSink(CompleteOrderRequest message) {
		String messagePayload = message.toString();
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
