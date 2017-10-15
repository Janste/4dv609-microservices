package se.lnu.services.cart;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.SendTo;

import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;
import se.lnu.service.common.message.RemoveFromCart;
import se.lnu.service.common.message.RequestCart;
import se.lnu.services.cart.data.CartRepository;

@SpringBootApplication
@EnableBinding(Inventory.class)
public class Application {
	private CartRepository cartRepository = new CartRepository();
	
	protected Logger logger = Logger.getLogger(Application.class.getName());
	
	@Autowired
	private Inventory inventory;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@StreamListener(Inventory.ADD_CART_INPUT)
	@SendTo(Inventory.ADDED_TO_CART_OUTPUT)
	public AddToCart processAddToCart(AddToCart request) {
		logger.info("Adding to cart: " + request);
		if (cartRepository.addToCart(request)) {
			request.setSuccess(true);
		}
		else {
			request.setSuccess(false);
			request.setError("Could not add to cart.");
		}
		return request;
	}
	
	@StreamListener(Inventory.REQUEST_CART_INPUT)
	@SendTo(Inventory.REQUEST_CART_OUTPUT)
	public RequestCart requestCart(RequestCart request) {
		request.setPetIDs(cartRepository.getCart(request));
		return request;
	}
	
	@StreamListener(Inventory.REMOVE_CART_INPUT)
	@SendTo(Inventory.REMOVE_CART_OUTPUT)
	public RemoveFromCart removeFromCart(RemoveFromCart request) {
		logger.info("Removing from cart: " + request);
		if (cartRepository.removeFromCart(request)) {
			request.setSuccess(true);
		}
		else {
			request.setSuccess(false);
			request.setError("Could not remove from cart.");
		}
		return request;
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
