package se.lnu.service.common.channels;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Inventory {
	String ADD_CART_INPUT = "add-cart-in";
	String ADD_CART_OUTPUT = "add-cart-ex";
	
	String REMOVE_CART_INPUT = "remove-cart-in";
	String REMOVE_CART_OUTPUT = "remove-cart-out";
	
	String ADDED_TO_CART_INPUT = "added-cart-in";
	String ADDED_TO_CART_OUTPUT = "added-cart-out";
	
	String REQUEST_INVENTORY_INPUT = "request-inv-in";
	String REQUEST_INVENTORY_OUTPUT = "request-inv-out";
	
	String REQUEST_CART_INPUT = "request-cart-in";
	String REQUEST_CART_OUTPUT = "request-cart-out";
	
	String COMPLETE_REQUEST_INPUT = "complete-request-in";
	String COMPLETE_REQUEST_OUTPUT = "complete-request-out";
	
	@Input(Inventory.ADD_CART_INPUT)
	SubscribableChannel input();
	
	@Output(Inventory.ADD_CART_OUTPUT)
	MessageChannel addToCartOutput();
	
	@Input(Inventory.REMOVE_CART_INPUT)
	SubscribableChannel removeCartInput();
	
	@Output(Inventory.REMOVE_CART_OUTPUT)
	MessageChannel removeCartOutput();
	
	@Input(Inventory.ADDED_TO_CART_INPUT)
	SubscribableChannel addedInput();
	
	@Output(Inventory.ADDED_TO_CART_OUTPUT)
	MessageChannel addedToCartOutput();
	
	@Input(Inventory.REQUEST_INVENTORY_INPUT)
	SubscribableChannel requestedInput();
	
	@Output(Inventory.REQUEST_INVENTORY_OUTPUT)
	MessageChannel requestInventoryOutput();
	
	@Input(Inventory.REQUEST_CART_INPUT)
	SubscribableChannel requestedCartInput();
	
	@Output(Inventory.REQUEST_CART_OUTPUT)
	MessageChannel requestCartOutput();
	
	@Input(Inventory.COMPLETE_REQUEST_INPUT)
	SubscribableChannel completeRequestInput();
	
	@Output(Inventory.COMPLETE_REQUEST_OUTPUT)
	MessageChannel completeRequestOutput();
}
