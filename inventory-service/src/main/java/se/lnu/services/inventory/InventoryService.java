package se.lnu.services.inventory;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
	
	// Needs REST API for client to query what is in the inventory/prices (no auth needed)
	// Database to store inventory
	// Event listener to hear a transaction complete and remove the pet from inventory
	// Send event when user adds pet to cart (to add it to cart from inventory)
	
	public InventoryService() {

	}

}
