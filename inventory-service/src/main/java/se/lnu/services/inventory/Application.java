package se.lnu.services.inventory;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

import se.lnu.service.common.animals.Pet;
import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;
import se.lnu.service.common.message.RequestPets;
import se.lnu.services.inventory.data.InventoryRepository;

@SpringBootApplication
@EnableBinding(Inventory.class)
public class Application {
	private InventoryRepository inventoryService = new InventoryRepository();
	
	@Autowired
	private Inventory inventory;
	
	@StreamListener(Inventory.ADD_CART_INPUT)
	public void processInventory(AddToCart request) throws InterruptedException {
		logger.info("Checking inventory: " + request);
		if (!inventoryService.petExists(request.getPet())) {
			request.setSuccess(false);
			request.setError("This pet doesn't exist!");
			inventory.addedToCartOutput().send(MessageBuilder.withPayload(request).build());
			return;
		}
		inventory.addToCartOutput().send(MessageBuilder.withPayload(request).build());
	}
	
	@StreamListener(Inventory.REQUEST_INVENTORY_INPUT)
	@SendTo(Inventory.REQUEST_INVENTORY_OUTPUT)
	public RequestPets requestInventory(RequestPets pets) {
		logger.info("Requesting pet inventory");
		pets.setPets(inventoryService.getPets());
		for (Pet pet : pets.getPets()) {
			System.out.println(pet.toString());
		}
		return pets;
	}
	
	protected Logger logger = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

}
