package com.health.util.kafka.producer;

import com.health.util.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing and sending user events to a Kafka topic.
 * This service will be used by other applications to send user-related events.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EventProducerService {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    // It's good practice to make the topic name configurable.
    // We'll use "user-topic" as specified, but this allows for future flexibility.
    @Value("${user.event.topic:user-topic}") // Default to "user-topic" if not in properties
    private String userTopic;

    /**
     * Sends a UserEventDto to the configured Kafka topic.
     * The user ID from the event data is used as the Kafka message key.
     *
     * @param userEvent The UserEventDto to be sent.
     * @throws IllegalArgumentException if userEvent, its userData, or userData.id() is null.
     */
    public void sendUserEvent(UserEvent userEvent) {
        if (userEvent == null || userEvent.userData() == null || userEvent.userData().id() == null) {
            String eventTypeInfo = (userEvent != null && userEvent.eventType() != null) ? userEvent.eventType() : "unknown";
            String userIdInfo = (userEvent != null && userEvent.userData() != null && userEvent.userData().id() != null) ? userEvent.userData().id().toString() : "unknown";

            log.error("Invalid UserEventDto provided. EventType: {}, UserID: {}. Event, its userData, or userData's ID cannot be null.", eventTypeInfo, userIdInfo);
            throw new IllegalArgumentException("Invalid UserEventDto: Event data is incomplete. Ensure event, user data, and user ID are provided.");
        }

        String userId = userEvent.userData().id().toString();
        String eventType = userEvent.eventType();

        try {
            log.info("Sending UserEventDto to topic '{}' with key '{}': EventType: {}, UserID: {}",
                    userTopic, userId, eventType, userId);
            kafkaTemplate.send(userTopic, userId, userEvent);
            log.info("UserEventDto sent successfully to topic '{}' for UserID: {}, EventType: {}", userTopic, userId, eventType);
        } catch (Exception e) {
            log.error("Error sending UserEventDto to topic '{}' for UserID: {}. EventType: {}. Error: {}",
                    userTopic, userId, eventType, e.getMessage(), e);
        }
    }
}