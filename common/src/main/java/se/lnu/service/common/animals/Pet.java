package se.lnu.service.common.animals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Dog.class, name = "Dog"),
    @JsonSubTypes.Type(value = Cat.class, name = "Cat")
})
public abstract class Pet {
	private int id;
	private String name;
	private int value;
	private String description;
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public abstract String getType();
	
	public abstract String getCastType();
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"@type\" : \"").append(getCastType()).append("\",");
		sb.append("\"type\" : \"").append(getType()).append("\",");
		sb.append("\"id\" : ").append(id).append(",");
		sb.append("\"name\" : \"").append(name).append("\",");
		sb.append("\"description\" : \"").append(description).append("\",");
		sb.append("\"value\" : ").append(value);
		sb.append("}");
		return sb.toString();
	}

}
