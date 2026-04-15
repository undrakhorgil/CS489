package edu.miu.cs.cs489.lab6.adsdentalsurgerycli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab 6 — Spring Boot CLI (no embedded web server). Console work runs via {@link CliSessionRunner}.
 * Layout mirrors the CityLibrary CLI: entities, repositories, services, sample load, {@code CommandLineRunner}.
 */
@SpringBootApplication
public class AdsDentalSurgeryCliApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdsDentalSurgeryCliApplication.class, args);
    }
}
