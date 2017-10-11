package se.lnu.service.common.animals;

public class Dog extends Pet {
	private SubType type;
	
	public Dog() { }

	public Dog(String name, int value, SubType type) {
		this.setName(name);
		this.setValue(value);
		this.type = type;
	}
	
	public void setType(SubType type) {
		this.type = type;
	}
	
	public String getType() {
		return type.toString();
	}
	
	public enum SubType {
		Chihuaha
	}

}
