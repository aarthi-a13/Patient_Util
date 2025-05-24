package com.health.util.kafka.consumer;

import com.health.util.audit.service.AuditPersistenceService;
import com.health.util.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for consuming user events from Kafka and processing them.
 * It listens to a specified Kafka topic, deserializes messages to UserEvent,
 * and then passes them to the AuditPersistenceService to be saved as audit records.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EventConsumerService {

    private final AuditPersistenceService auditPersistenceService;

    /**
     * Listens to the user event topic for messages.
     * The topic and group ID are configured in application.properties.
     * 
     * @param userEvent The user event received from Kafka
     */
    @KafkaListener(topics = "${user.event.topic:user-topic}", groupId = "user-group")
    public void consumeUserEvent(UserEvent userEvent) {
        if (userEvent == null) {
            log.warn("Received null UserEvent message. Skipping.");
            return;
        }

        String userId = userEvent.userData() != null ? userEvent.userData().id().toString() : "N/A";
        String eventType = userEvent.eventType();
        
        log.info("Received UserEvent: EventType='{}', UserID='{}'", eventType, userId);

        try {
            // Process the user event
            auditPersistenceService.persistUserEventAudit(userEvent, "KafkaConsumer");
            log.info("Successfully processed UserEvent for UserID: {}, EventType: {}", userId, eventType);
        } catch (Exception e) {
            log.error("Error processing UserEvent for UserID: {}, EventType: {}. Error: {}",
                    userId, eventType, e.getMessage(), e);
            // With auto-commit enabled (default), failed messages will be considered processed
        }
    }
}
