package se.lnu.service.common.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestUser {
	private String channelId;
	private User user;
	private boolean success;
	private String error;
	
	private Type type;
	
	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"type\" : \"").append(type.toString()).append("\",");
		if (success) {
			sb.append("\"user\" : ").append(user.toString()).append(",");
			sb.append("\"success\" :").append(success);
		}
		else {
			sb.append("\"success\" :").append(success).append(",");
			sb.append("\"error\" : \"").append(error).append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public enum Type {
		register,
		login,
		getUser,
		updateUser
	}
	
}
