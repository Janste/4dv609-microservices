package se.lnu.service.common.message;

import java.util.List;

import se.lnu.service.common.animals.Pet;

public class RequestCart {
	private List<Integer> petIds;
	private List<Pet> pets;
	private String userEmail;
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserId(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public void setPetIDs(List<Integer> ids) {
		petIds = ids;
	}
	
	public List<Integer> getPetIDs() {
		return petIds;
	}
	
	public void setPets(List<Pet> pets) {
		this.pets = pets;
	}
	
	public List<Pet> getPets() {
		return pets;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"pets\" : [");
		if (pets != null) {
			boolean first = true;
			for (Pet pet : pets) {
				if (!first) {
					sb.append(",");
				}
				else
					first = false;
				sb.append(pet.toString());
			}
		}
		sb.append("]}");
		return sb.toString();
	}
}
