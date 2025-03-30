package com.example.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Abstract base class for Kafka producers implementing different delivery modes.
 * Provides common functionality for sending messages to Kafka topics.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public abstract class AbstractKafkaProducer<K, V> {
    
    protected final KafkaTemplate<K, V> kafkaTemplate;
    protected final String topic;

    /**
     * Constructs a new AbstractKafkaProducer with the specified KafkaTemplate and topic.
     *
     * @param kafkaTemplate The KafkaTemplate to use for sending messages
     * @param topic The topic to send messages to
     */
    protected AbstractKafkaProducer(KafkaTemplate<K, V> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * Sends a message to the configured topic without a key.
     *
     * @param message The message to send
     * @return A CompletableFuture containing the SendResult
     */
    public CompletableFuture<SendResult<K, V>> send(V message) {
        return kafkaTemplate.send(topic, message);
    }

    /**
     * Sends a message to the configured topic with a key.
     *
     * @param key The key for the message
     * @param message The message to send
     * @return A CompletableFuture containing the SendResult
     */
    public CompletableFuture<SendResult<K, V>> send(K key, V message) {
        return kafkaTemplate.send(topic, key, message);
    }

    /**
     * Adds a callback to handle the result of sending a message.
     *
     * @param future The CompletableFuture from the send operation
     * @param callback The callback to handle the result
     */
    protected void addCallback(CompletableFuture<SendResult<K, V>> future, 
                             BiConsumer<SendResult<K, V>, Throwable> callback) {
        future.whenComplete(callback);
    }
} 