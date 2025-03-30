package com.example.kafka.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String TOPIC = "test-topic";
    private static final String MESSAGE = "test message";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private AtMostOnceProducer<String, String> atMostOnceProducer;
    private AtLeastOnceProducer<String, String> atLeastOnceProducer;
    private ExactlyOnceProducer<String, String> exactlyOnceProducer;

    @BeforeEach
    void setUp() {
        atMostOnceProducer = new AtMostOnceProducer<>(kafkaTemplate, TOPIC);
        atLeastOnceProducer = new AtLeastOnceProducer<>(kafkaTemplate, TOPIC);
        exactlyOnceProducer = new ExactlyOnceProducer<>(kafkaTemplate, TOPIC);
    }

    @Test
    void testAtMostOnceProducer() {
        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(eq(TOPIC), eq(MESSAGE))).thenReturn(future);

        atMostOnceProducer.send(MESSAGE);

        verify(kafkaTemplate).send(eq(TOPIC), eq(MESSAGE));
    }

    @Test
    void testAtLeastOnceProducer() {
        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(eq(TOPIC), eq(MESSAGE))).thenReturn(future);

        atLeastOnceProducer.send(MESSAGE);

        verify(kafkaTemplate).send(eq(TOPIC), eq(MESSAGE));
    }

    @Test
    void testExactlyOnceProducer() {
        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(eq(TOPIC), eq(MESSAGE))).thenReturn(future);
        when(kafkaTemplate.executeInTransaction(any())).thenAnswer(invocation -> {
            KafkaOperations.OperationsCallback<String, String, Object> callback = invocation.getArgument(0);
            return callback.doInOperations(kafkaTemplate);
        });

        exactlyOnceProducer.send(MESSAGE);

        verify(kafkaTemplate).executeInTransaction(any());
    }

    @Test
    void testAtLeastOnceProducerRetry() {
        SendResult<String, String> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
        failureFuture.completeExceptionally(new RuntimeException("Test failure"));
        CompletableFuture<SendResult<String, String>> successFuture = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(eq(TOPIC), eq(MESSAGE)))
            .thenReturn(failureFuture)
            .thenReturn(successFuture);

        atLeastOnceProducer.send(MESSAGE);

        verify(kafkaTemplate, times(2)).send(eq(TOPIC), eq(MESSAGE));
    }
} 