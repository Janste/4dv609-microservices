package se.lnu.services.inventory;

import org.springframework.stereotype.Service;

import se.lnu.services.inventory.data.InventoryRepository;

@Service
public class InventoryService {
	private InventoryRepository repository;
	
	// Needs REST API for client to query what is in the inventory/prices (no auth needed)
	// Database to store inventory
	// Event listener to hear a transaction complete and remove the pet from inventory
	// Send event when user adds pet to cart (to add it to cart from inventory)
	
	public InventoryService() {
		repository = new InventoryRepository();
	}

}
