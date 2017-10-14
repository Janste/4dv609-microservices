package se.lnu.services.cart.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.lnu.service.common.message.AddToCart;

public class CartAccessLayer
{
	private Connection connection = null;
    private static CartAccessLayer instance = null;

    private CartAccessLayer() {

        String url = "jdbc:mysql://localhost:3306/cart_service";
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

    public static CartAccessLayer getInstance() {
        if (instance == null) {
            instance = new CartAccessLayer();
        }
        return instance;
    }
    
    public List<Integer> getCartContents(String email) {
    	List<Integer> res = new ArrayList<>();
    	
    	try {
            String query = "SELECT * FROM CART WHERE ownerEmail = ?;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
            	res.add(result.getInt("pet"));
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return res;
    }
    
    public boolean addToCart(AddToCart request) {
    	boolean found = false;
    	try {
    		String query = "INSERT INTO CART VALUES (?, ?, ?);";
    		PreparedStatement statement = connection.prepareStatement(query);
    		statement.setString(2, request.getUserEmail());
    		statement.setInt(3, request.getPet().getID());
    		int result = statement.executeUpdate();
    		if (result > 0) {
    			found = true;
    		}
    		statement.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return found;
    }

}
