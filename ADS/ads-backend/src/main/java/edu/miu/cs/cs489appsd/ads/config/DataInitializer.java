package edu.miu.cs.cs489appsd.ads.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(DatabaseSeeder databaseSeeder) {
        return args -> databaseSeeder.seed();
    }
}
