package se.lnu.services.inventory.data;

import java.util.List;

import se.lnu.service.common.animals.Pet;

public class InventoryRepository
{
    public boolean removePet(Pet pet) {
    	//TODO
    	return false;
    }
    
    public boolean petExists(Pet pet) {
    	return InventoryAccessLayer.getInstance().petExists(pet);
    }
    
    public List<Pet> getPets() {
    	return InventoryAccessLayer.getInstance().getPets();
    }
}
