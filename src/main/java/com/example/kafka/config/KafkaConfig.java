package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka producers and topics.
 * Configures three different KafkaTemplates for different delivery semantics:
 * - At-most-once delivery
 * - At-least-once delivery
 * - Exactly-once delivery
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a KafkaTemplate configured for at-most-once delivery.
     * Messages may be lost but will never be delivered more than once.
     */
    @Bean
    public KafkaTemplate<String, String> atMostOnceTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "0"); // No acknowledgment required
        configProps.put(ProducerConfig.RETRIES_CONFIG, 0); // No retries

        ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(factory);
    }

    /**
     * Creates a KafkaTemplate configured for at-least-once delivery.
     * Messages will be delivered at least once, with retries on failure.
     */
    @Bean
    public KafkaTemplate<String, String> atLeastOnceTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry up to 3 times

        ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(factory);
    }

    /**
     * Creates a KafkaTemplate configured for exactly-once delivery.
     * Uses idempotent producer and transaction management.
     */
    @Bean
    public KafkaTemplate<String, String> exactlyOnceTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Enable idempotence
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "exactly-once-producer");

        ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(factory);
        template.setTransactionIdPrefix("exactly-once-");
        return template;
    }

    /**
     * Creates a topic for at-most-once delivery messages.
     */
    @Bean
    public NewTopic atMostOnceTopic() {
        return new NewTopic("at-most-once-topic", 3, (short) 3);
    }

    /**
     * Creates a topic for at-least-once delivery messages.
     */
    @Bean
    public NewTopic atLeastOnceTopic() {
        return new NewTopic("at-least-once-topic", 3, (short) 3);
    }

    /**
     * Creates a topic for exactly-once delivery messages.
     */
    @Bean
    public NewTopic exactlyOnceTopic() {
        return new NewTopic("exactly-once-topic", 3, (short) 3);
    }
} 