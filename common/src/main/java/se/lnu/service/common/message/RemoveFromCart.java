package se.lnu.service.common.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import se.lnu.service.common.animals.Pet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoveFromCart {
	private String userEmail;
	private Pet pet;
	private boolean success;
	private String error;
	
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
	
	public void setSuccess(boolean succ) {
		this.success = succ;
	}
	
	public boolean getSuccess() {
		return success;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"type\" : \"removeFromCart\",");
		sb.append("\"pet\" : ").append(pet.toString()).append(",");
		sb.append("\"success\" : ").append(success);
		if (!success)
			sb.append(",").append("\"error\" : \"").append(error).append("\"");
		sb.append("}");
		return sb.toString();
	}
}
