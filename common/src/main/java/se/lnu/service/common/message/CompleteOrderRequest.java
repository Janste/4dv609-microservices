package se.lnu.service.common.message;

import java.util.List;

public class CompleteOrderRequest {
	private String userEmail;
	private boolean success;
	private String error;
	private List<Integer> petIds;
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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
	
	public void setPetIDs(List<Integer> ids) {
		petIds = ids;
	}
	
	public List<Integer> getPetIDs() {
		return petIds;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"type\" : \"completeOrder\",");
		sb.append("\"success\" : ").append(success);
		if (!success)
			sb.append(",").append("\"error\" : \"").append(error).append("\"");
		sb.append("}");
		return sb.toString();
	}
}
