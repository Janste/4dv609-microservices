package se.lnu.services.cart.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.lnu.service.common.animals.Pet;
import se.lnu.service.common.message.AddToCart;
import se.lnu.service.common.message.CompleteOrderRequest;
import se.lnu.service.common.message.RemoveFromCart;

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
    	List<Integer> res = new ArrayList<Integer>();
    	
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
    		statement.setInt(1, 0);
    		statement.setString(2, request.getUserEmail());
    		statement.setInt(3, request.getPet().getID());
    		int result = statement.executeUpdate();
    		if (result > 0) {
    			found = true;
    		}
    		statement.close();
    		connection.commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return found;
    }
    
    public boolean removePet(RemoveFromCart request) {
    	boolean success = false;
        try {
            String sql = "DELETE FROM CART WHERE ownerEmail = ? AND pet = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getUserEmail());
            ps.setInt(2, request.getPet().getID());
            
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
    
    public boolean emptyCart(CompleteOrderRequest request) {
    	boolean success = false;
    	List<Integer> res = getCartContents(request.getUserEmail());
    	if (res.size() <= 0)
    		return false;
        try {
            String sql = "DELETE FROM CART WHERE ownerEmail = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getUserEmail());
            
            int num = ps.executeUpdate();
            if (num > 0) {
            	success = true;
            }
            ps.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.setPetIDs(res);
        
        return success;
    }

}
