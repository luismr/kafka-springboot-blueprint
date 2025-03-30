package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Kafka delivery modes demonstration.
 * This application demonstrates three different Kafka delivery modes:
 * - At-most-once delivery
 * - At-least-once delivery
 * - Exactly-once delivery
 */
@SpringBootApplication
public class KafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }
} 