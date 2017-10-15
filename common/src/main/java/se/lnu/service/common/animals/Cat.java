package se.lnu.service.common.animals;

public class Cat extends Pet {
	private SubType type;
	
	public Cat() { }
	
	public Cat(String name, int value, SubType type) {
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
		Siberian,
		MaineCoon
	}

	@Override
	public String getCastType() {
		return "Cat";
	}
}
