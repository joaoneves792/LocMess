package LocMess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by joao on 3/25/17.
 */
@SpringBootApplication
public class ServerApp {
    public static void main(String[] args) {
        System.setProperty("server.port", "8080");
        System.setProperty("server.ssl.key-store", "classpath:keystore.jks");
        System.setProperty("server.ssl.key-store-password", "123456");
        System.setProperty("server.ssl.key-password", "123456");
        System.setProperty("server.ssl.enabled", "true");

        SpringApplication.run(ServerApp.class, args);
    }
}
