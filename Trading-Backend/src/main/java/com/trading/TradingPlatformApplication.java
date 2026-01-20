package com.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Trading Platform
 * This is a mini Spring Boot project demonstrating core trading operations
 * for B.Tech students
 */
@SpringBootApplication
public class TradingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingPlatformApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Trading Platform Started Successfully!");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("===========================================\n");
    }
}
