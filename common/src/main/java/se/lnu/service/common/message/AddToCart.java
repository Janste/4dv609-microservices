package se.lnu.service.common.message;

import se.lnu.service.common.animals.Pet;

public class AddToCart {
	private long userId;
	private Pet pet;
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Pet getPet() {
		return pet;
	}
	
	public void setPet(Pet pet) {
		this.pet = pet;
	}
	
	@Override
	public String toString() {
		return userId + " " + pet.toString();
	}
}
