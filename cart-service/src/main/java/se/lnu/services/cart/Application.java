package se.lnu.services.cart;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;

import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;

@SpringBootApplication
@EnableBinding(Inventory.class)
public class Application {
	
	// Needs
	// Rest API for querying contents
	// Event listener to hear when item is added from inventory
	// Event listener to empty when payment complete
	
	protected Logger logger = Logger.getLogger(Application.class.getName());
	
	@Autowired
	Inventory inventory;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@StreamListener(Inventory.ADD_CART_INPUT)
	public void processAddToCart(AddToCart request) {
		logger.info("Adding to cart: " + request);
		request.setSuccess(true);
		inventory.addedToCartOutput().send(MessageBuilder.withPayload(request).build());
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
