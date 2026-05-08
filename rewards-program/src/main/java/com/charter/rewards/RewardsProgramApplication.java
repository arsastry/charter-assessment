package com.charter.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Rewards Program Spring Boot application.
 * <p>
 * This application calculates reward points for customer transactions
 * based on the charter rewards program rules.
 * </p>
 */
@SpringBootApplication
public class RewardsProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardsProgramApplication.class, args);
    }
}

