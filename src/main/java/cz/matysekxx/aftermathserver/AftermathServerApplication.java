package cz.matysekxx.aftermathserver;

import cz.matysekxx.aftermathserver.action.UseAction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AftermathServerApplication {
    public static void main(String[] args) {
        UseAction useAction = new UseAction();
        SpringApplication.run(AftermathServerApplication.class, args);
    }

}
