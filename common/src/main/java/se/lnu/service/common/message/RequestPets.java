package se.lnu.service.common.message;

import java.util.List;

import se.lnu.service.common.animals.Pet;

public class RequestPets {
	private String connectionId;
	private List<Pet> pets;
	
	public void setConnectionID(String id) {
		this.connectionId = id;
	}
	
	public String getConnectionID() {
		return connectionId;
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
		sb.append("{ \"type\" : \"requestInventory\", \"pets\" : [");
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
