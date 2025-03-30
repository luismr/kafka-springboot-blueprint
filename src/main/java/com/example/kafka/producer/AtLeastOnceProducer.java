package com.example.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Kafka producer with at-least-once delivery semantics.
 * Messages are retried on failure to ensure delivery, but may be delivered multiple times.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class AtLeastOnceProducer<K, V> extends AbstractKafkaProducer<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(AtLeastOnceProducer.class);
    private static final int MAX_RETRIES = 3;

    /**
     * Constructs a new AtLeastOnceProducer.
     *
     * @param kafkaTemplate The KafkaTemplate to use for sending messages
     * @param topic The topic to send messages to
     */
    public AtLeastOnceProducer(KafkaTemplate<K, V> kafkaTemplate, String topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public CompletableFuture<SendResult<K, V>> send(V message) {
        return sendWithRetry(message, null, 0);
    }

    @Override
    public CompletableFuture<SendResult<K, V>> send(K key, V message) {
        return sendWithRetry(message, key, 0);
    }

    private CompletableFuture<SendResult<K, V>> sendWithRetry(V message, K key, int retryCount) {
        CompletableFuture<SendResult<K, V>> future = key != null ? super.send(key, message) : super.send(message);
        addCallback(future, (result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send message: {}", message, ex);
                if (retryCount < MAX_RETRIES) {
                    logger.info("Retrying message. Attempt {}/{}", retryCount + 1, MAX_RETRIES);
                    sendWithRetry(message, key, retryCount + 1);
                } else {
                    logger.error("Max retries ({}) reached for message: {}", MAX_RETRIES, message);
                }
            } else {
                logger.debug("Message sent successfully: {}", result.getProducerRecord().value());
            }
        });
        return future;
    }
} 