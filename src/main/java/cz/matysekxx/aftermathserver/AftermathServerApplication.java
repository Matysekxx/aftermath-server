package cz.matysekxx.aftermathserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Aftermath Server.
 * This class is the entry point for the Spring Boot application.
 *
 * @author Matysekxx
 */
@EnableScheduling
@SpringBootApplication
public class AftermathServerApplication {
    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(AftermathServerApplication.class, args);
    }

}
