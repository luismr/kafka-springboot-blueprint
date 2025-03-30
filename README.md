# Kafka Spring Boot Blueprint

This project demonstrates three different Kafka delivery modes using Spring Boot:
- At-most-once delivery
- At-least-once delivery
- Exactly-once delivery

## Getting Started

### Cloning the Repository

```bash
git clone git@github.com:luismr/kafka-springboot-blueprint.git
cd kafka-springboot-blueprint
```

## Project Structure

The project consists of the following main components:

1. **Abstract Producer**: `AbstractKafkaProducer` - Base class providing common functionality for all producers
2. **Delivery Mode Implementations**:
   - `AtMostOnceProducer` - Messages may be lost but never delivered more than once
   - `AtLeastOnceProducer` - Messages will be delivered at least once, with retries
   - `ExactlyOnceProducer` - Messages will be delivered exactly once, with no duplicates

3. **Configuration**:
   - `KafkaConfig` - Configures Kafka templates and topics
   - `application.yml` - Application properties with detailed comments

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- Apache Kafka 3.x
- Spring Boot 3.x

## Configuration

The application can be configured using `application.yml` or environment variables.

### Using application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      # Delivery mode specific configurations
      at-most-once:
        acks: 0
      at-least-once:
        acks: all
        retries: 3
        retry-backoff-ms: 1000
      exactly-once:
        acks: all
        enable.idempotence: true
        transaction.timeout.ms: 30000
        max.in.flight.requests.per.connection: 5
```

### Using Environment Variables

You can configure the Kafka bootstrap servers using environment variables on different operating systems:

#### Linux/macOS
```bash
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

#### Windows (Command Prompt)
```cmd
set SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

#### Windows (PowerShell)
```powershell
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
```

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
mvn clean test
```

## Delivery Modes

### At-most-once Delivery

- Messages may be lost but will never be delivered more than once
- No acknowledgment required from brokers
- No retries on failure
- Best for scenarios where message loss is acceptable

### At-least-once Delivery

- Messages will be delivered at least once
- Acknowledgment required from all replicas
- Retries on failure
- May result in duplicate messages
- Best for scenarios where duplicates are acceptable but message loss is not

### Exactly-once Delivery

- Messages will be delivered exactly once
- Uses idempotent producer and transaction management
- No duplicates and no message loss
- Best for scenarios requiring strict message delivery guarantees

## Configuration

The `application.yml` file contains detailed configuration for each delivery mode:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      # At-most-once configuration
      at-most-once:
        acks: 0
        retries: 0
      
      # At-least-once configuration
      at-least-once:
        acks: all
        retries: 3
      
      # Exactly-once configuration
      exactly-once:
        acks: all
        enable-idempotence: true
        transaction-id-prefix: exactly-once-
```

## Testing

The project includes unit tests for each producer implementation:
- `testAtMostOnceProducer()`
- `testAtLeastOnceProducer()`
- `testExactlyOnceProducer()`
- `testAtLeastOnceProducerRetry()`

Tests use Mockito to mock the KafkaTemplate and verify producer behavior. 

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please make sure to update tests as appropriate and follow the existing code style.

## Running Kafka Locally

### Single Node Setup

For local development, you can run a single Kafka broker using Docker:

```bash
docker run -d \
  --name kafka \
  -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_NODE_ID=0 \
  -e KAFKA_PROCESS_ROLES=controller,broker \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=0@kafka:9093 \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  -e KAFKA_AUTO_CREATE_TOPICS_ENABLE=true \
  confluentinc/cp-kafka:7.5.1
```

This setup:
- Uses KRaft (Kafka Raft metadata mode) instead of Zookeeper
- Exposes port 9092 for client connections
- Enables auto topic creation
- Configures the broker for local development
- Uses the Confluent Kafka image

### Cluster Setup

A Docker Compose setup for running a Kafka cluster locally is available at [kafka-cluster-docker-compose](https://github.com/luismr/kafka-cluster-docker-copose). This setup includes:
- Multiple Kafka brokers using KRaft (Kafka Raft metadata mode)
- Kafdrop (Web UI for Kafka)
- Network configuration for local development

## License

This project is licensed under the MIT License - see the LICENSE file for details. 