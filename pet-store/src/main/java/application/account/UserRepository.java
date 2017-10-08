package application.account;

import java.util.ArrayList;

// Currently this is a dummy repository/database

public class UserRepository
{
    private ArrayList<User> users = new ArrayList<User>();

    public void registerUser(User user) {
        users.add(user);
    }
}
