package se.lnu.service.common.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    private String firstName;
    private String secondName;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String telephone;
    private String email;
    private String password;
    private String token;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getSecondName()
    {
        return secondName;
    }

    public void setSecondName(String secondName)
    {
        this.secondName = secondName;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean comma = false;
		if (firstName != null) {
			sb.append("\"firstName\" : \"").append(firstName).append("\"");
			comma = true;
		}
		if (secondName != null) {
			if (comma)
				sb.append(",");
			sb.append("\"secondName\" : \"").append(secondName).append("\"");
			comma = true;
		}
		if (streetAddress != null) {
			if (comma)
				sb.append(",");
			sb.append("\"streetAddress\" : \"").append(streetAddress).append("\"");
			comma = true;
		}
		if (city != null) {
			if (comma)
				sb.append(",");
			sb.append("\"city\" : \"").append(city).append("\"");
			comma = true;
		}
		if (state != null) {
			if (comma)
				sb.append(",");
			sb.append("\"state\" : \"").append(state).append("\"");
			comma = true;
		}
		if (zipCode != null) {
			if (comma)
				sb.append(",");
			sb.append("\"zipCode\" : \"").append(zipCode).append("\"");
			comma = true;
		}
		if (country != null) {
			if (comma)
				sb.append(",");
			sb.append("\"country\" : \"").append(country).append("\"");
			comma = true;
		}
		if (telephone != null) {
			if (comma)
				sb.append(",");
			sb.append("\"telephone\" : \"").append(telephone).append("\"");
			comma = true;
		}
		if (email != null) {
			if (comma)
				sb.append(",");
			sb.append("\"email\" : \"").append(email).append("\"");
			comma = true;
		}
		if (token != null) {
			if (comma)
				sb.append(",");
			sb.append("\"token\" : \"").append(token).append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
}
