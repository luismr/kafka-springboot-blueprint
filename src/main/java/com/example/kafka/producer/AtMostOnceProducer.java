package com.example.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Kafka producer with at-most-once delivery semantics.
 * Messages are sent without waiting for acknowledgment and no retries are performed.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class AtMostOnceProducer<K, V> extends AbstractKafkaProducer<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(AtMostOnceProducer.class);

    /**
     * Constructs a new AtMostOnceProducer.
     *
     * @param kafkaTemplate The KafkaTemplate to use for sending messages
     * @param topic The topic to send messages to
     */
    public AtMostOnceProducer(KafkaTemplate<K, V> kafkaTemplate, String topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public CompletableFuture<SendResult<K, V>> send(V message) {
        CompletableFuture<SendResult<K, V>> future = super.send(message);
        addCallback(future, (result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send message", ex);
            } else {
                logger.debug("Message sent successfully: {}", result.getProducerRecord().value());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<SendResult<K, V>> send(K key, V message) {
        CompletableFuture<SendResult<K, V>> future = super.send(key, message);
        addCallback(future, (result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send message", ex);
            } else {
                logger.debug("Message sent successfully: {}", result.getProducerRecord().value());
            }
        });
        return future;
    }
} 