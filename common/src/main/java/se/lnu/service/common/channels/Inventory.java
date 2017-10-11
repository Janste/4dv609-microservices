package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Inventory {
	String ADD_CART_INPUT = "add-cart-in";
	String ADD_CART_OUTPUT = "add-cart-ex";
	
	@Input(Inventory.ADD_CART_INPUT)
	SubscribableChannel input();
	
	@Output(Inventory.ADD_CART_OUTPUT)
	MessageChannel addToCartOutput();
}
