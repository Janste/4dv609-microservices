package se.lnu.services.inventory.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.lnu.service.common.animals.Cat;
import se.lnu.service.common.animals.Dog;
import se.lnu.service.common.animals.Pet;

public class InventoryAccessLayer
{
	private Connection connection = null;
    private static InventoryAccessLayer instance = null;

    private InventoryAccessLayer() {

        String url = "jdbc:mysql://localhost:3306/inventory_service";
        String username = "root";
        String password = "my_password";

        try
        {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static InventoryAccessLayer getInstance() {
        if (instance == null) {
            instance = new InventoryAccessLayer();
        }
        return instance;
    }
    
    public boolean petExists(Pet pet) {
    	boolean found = false;
    	try {
    		String query = "SELECT * FROM INVENTORY WHERE id = ?;";

    		PreparedStatement statement = connection.prepareStatement(query);
    		statement.setInt(1, pet.getID());
    		ResultSet result = statement.executeQuery();
    		if (result.next()) {
    			System.out.println(result.getString("name"));
    			found = true;
    		}
    		result.close();
    		statement.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return found;
    }
    
    public List<Pet> getCartPets(List<Integer> petIDs) {
    	ArrayList<Pet> res = new ArrayList<Pet>();
    	
    	if (petIDs.size() == 0)
    		return res;
    	
    	try {
    		StringBuilder petQuery = new StringBuilder();
    		boolean first = true;
    		for (int i : petIDs) {
    			if (first)
    				first = false;
    			else
    				petQuery.append(",");
    			petQuery.append(i);
    		}
            String query = "SELECT * FROM INVENTORY WHERE ID IN (" + petQuery.toString() + ");";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
            	Pet next = null;
                String type = result.getString("type");
                switch (type) {
                case "Cat":
                	next = new Cat();
                	((Cat)next).setType(Cat.SubType.valueOf(result.getString("breed")));
                	break;
                case "Dog":
                	next = new Dog();
                	((Dog)next).setType(Dog.SubType.valueOf(result.getString("breed")));
                	break;
                default:
                	throw new IllegalArgumentException();
                }
                next.setID(result.getInt("id"));
            	next.setDescription(result.getString("description"));
            	next.setName(result.getString("name"));
            	next.setValue(result.getInt("value"));
                res.add(next);
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return res;
    }
    
    public List<Pet> getPets() {
    	ArrayList<Pet> res = new ArrayList<>();
    	
    	try {
            String query = "SELECT * FROM INVENTORY;";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
            	Pet next = null;
                String type = result.getString("type");
                switch (type) {
                case "Cat":
                	next = new Cat();
                	((Cat)next).setType(Cat.SubType.valueOf(result.getString("breed")));
                	break;
                case "Dog":
                	next = new Dog();
                	((Dog)next).setType(Dog.SubType.valueOf(result.getString("breed")));
                	break;
                default:
                	throw new IllegalArgumentException();
                }
                next.setID(result.getInt("id"));
            	next.setDescription(result.getString("description"));
            	next.setName(result.getString("name"));
            	next.setValue(result.getInt("value"));
                res.add(next);
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return res;
    }

    public boolean removePet(Pet pet) {
    	boolean success = false;
        try {
            String sql = "DELETE FROM INVENTORY WHERE id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, pet.getID());
            
            int num = ps.executeUpdate();
            if (num > 0) {
            	success = true;
            }
            ps.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

}
