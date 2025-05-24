# Healthcare Utility Library

A comprehensive utility library for healthcare applications built with Spring Boot 3.3.0. This library provides reusable components for event handling, Kafka messaging, and audit logging.

## Overview

The Healthcare Utility library is designed to be integrated into healthcare applications to provide common functionality such as:

- User event handling with Java records
- Kafka messaging for event publishing and consumption
- Audit logging of user-related activities
- Persistence of audit records using Spring Data JPA

## Features

### Event Handling

- **User Model**: Comprehensive user data model using Java records
- **Event Model**: Event wrapper for user data with event type information

### Kafka Integration

- **Event Producer**: Service for publishing user events to Kafka topics
- **Event Consumer**: Service for consuming user events from Kafka topics
- **Configurable Topics**: Kafka topics configurable via application properties

### Audit System

- **Audit Records**: Entity for storing user event audit information
- **Persistence Service**: Service for converting events to audit records and persisting them
- **Repository Layer**: JPA repository for database operations on audit records

## Prerequisites

- Java 21
- Maven 3.x
- PostgreSQL database
- Kafka broker

## Installation

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.health</groupId>
    <artifactId>util</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Configuration

### Database Configuration

Configure your PostgreSQL database connection in your application's `application.properties` or `application.yml`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Kafka Configuration

Configure Kafka in your application's `application.properties` or `application.yml`:

```properties
# Kafka Producer Configuration
spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Kafka Consumer Configuration
spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=user-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.health.util.event

# Topic Configuration
user.event.topic=user-topic
```

## Usage Examples

### Producing User Events

```java
import com.health.util.event.User;
import com.health.util.event.UserEvent;
import com.health.util.kafka.producer.EventProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final EventProducerService eventProducerService;

    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        // Create a user event
        UserEvent userEvent = new UserEvent("USER_CREATED", user);
        
        // Send the event to Kafka
        eventProducerService.sendUserEvent(userEvent);
    }
}
```

### Consuming User Events

The library automatically consumes user events from the configured Kafka topic and persists them as audit records. No additional code is required to enable this functionality.

### Querying Audit Records

```java
import com.health.util.audit.entity.AuditRecord;
import com.health.util.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuditController {

    private final AuditRepository auditRepository;

    @GetMapping("/audits/user/{userId}")
    public List<AuditRecord> getUserAudits(@PathVariable String userId) {
        log.info("Fetching audit records for user: {}", userId);
        return auditRepository.findByUserId(userId);
    }

    @GetMapping("/audits/event/{eventType}")
    public List<AuditRecord> getEventTypeAudits(@PathVariable String eventType) {
        log.info("Fetching audit records for event type: {}", eventType);
        return auditRepository.findByEventType(eventType);
    }
}
```

## Building from Source

To build the library from source:

```bash
mvn clean install
```

This will compile the code, run tests, and install the artifact to your local Maven repository.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
