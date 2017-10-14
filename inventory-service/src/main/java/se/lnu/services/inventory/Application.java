package se.lnu.services.inventory;

import java.util.Random;
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

	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private Inventory inventory;
	
	@StreamListener(Inventory.ADD_CART_INPUT)
	public void processInventory(AddToCart request) throws InterruptedException {
		logger.info("Checking inventory: " + request);
		Thread.sleep(2000);
		//TODO check if valid inventory
		if (new Random().nextInt(20) > 10) {
			request.setSuccess(false);
			request.setError("This pet doesn't exist!");
			inventory.addedToCartOutput().send(MessageBuilder.withPayload(request).build());
			return;
		}
		inventory.addToCartOutput().send(MessageBuilder.withPayload(request).build());
	}
	
	protected Logger logger = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	// TODO bind to listener add params etc
	public void OnPaymentCompleteEvent() {
		
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
