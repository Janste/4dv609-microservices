package se.lnu.service.common.message;

import se.lnu.service.common.animals.Pet;

public class AddToCart {
	private String userEmail;
	private Pet pet;
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserId(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public Pet getPet() {
		return pet;
	}
	
	public void setPet(Pet pet) {
		this.pet = pet;
	}
	
	@Override
	public String toString() {
		return userEmail + " " + pet.toString();
	}
}
