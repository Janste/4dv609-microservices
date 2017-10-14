package se.lnu.services.user;

import se.lnu.service.common.message.User;
import se.lnu.service.common.JWTAuth.TokenHandler;;

public class UserRepository {
	
    public User registerUser(User user) {
    	
    	if (UserAccessLayer.getInstance().registerUser(user)) {
    		user.setToken(TokenHandler.generateToken(user));
    		return user;
    	}
    	return null;	
    }
    
    public User loginUser(String email, String password) {
    	
    	User user = UserAccessLayer.getInstance().loginUser(email, password);
    	
    	if (user != null)
    		user.setToken(TokenHandler.generateToken(user));
    	
    	return user;
    }
    
    public boolean changeUser(User user) {
    	return UserAccessLayer.getInstance().changeUser(user);
    }
    
    public User getUserByEmail(String email) {
    	return UserAccessLayer.getInstance().getUserByEmail(email);
    }
}
