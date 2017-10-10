package se.lnu.services.user;

import se.lnu.service.common.message.User;

public class UserRepository
{
    public boolean registerUser(User user) {
        if (UserAccessLayer.getInstance().saveUser(user))
                return true;
        else
            return false;
    }
}
