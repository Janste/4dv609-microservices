package application;

import application.account.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.Environment;
import reactor.bus.EventBus;

import static reactor.bus.selector.Selectors.$;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {

    private static String email = "jack.sparrow@gmail.com";

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Authentication authentication;

    @Autowired
    private Registration registration;

    public void run(String... args) throws Exception {
        eventBus.on($("registerNewUser"), registration);
        authentication.notifyRegisterNewUser(email);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(Application.class, args);

        app.getBean(Environment.class).shutdown();
    }

}