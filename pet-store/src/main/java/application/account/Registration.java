package application.account;

import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class Registration implements Consumer<Event<User>>
{
    private UserRepository userRepository = new UserRepository();

    public void accept(Event<User> userEvent)
    {
        System.out.println("Registering new account");

        // not yet implemented
        //userRepository.registerUser(userEvent.getData());
        System.out.println("User registered");
    }
}
