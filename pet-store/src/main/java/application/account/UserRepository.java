package application.account;

import java.util.ArrayList;

public class UserRepository
{
    public boolean registerUser(User user) {
        if (UserAccessLayer.getInstance().saveUser(user))
                return true;
        else
            return false;
    }
}
