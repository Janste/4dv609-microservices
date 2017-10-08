package application;

import application.account.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class Authentication
{
    @Autowired
    EventBus eventBus;

    public void notifyRegisterNewUser(String email) {

        User user = new User();
        user.setFirstName("Jack");
        user.setSecondName("Sparrow");
        user.setEmail(email);

        eventBus.notify("registerNewUser", Event.wrap(user));

        System.out.println("Notified about new account being registered");
    }
}
