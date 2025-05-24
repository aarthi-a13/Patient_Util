package com.health.util.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.health.util.audit.entity.AuditRecord;
import com.health.util.audit.repository.AuditRepository;
import com.health.util.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for persisting audit records to the database.
 * It converts event DTOs into AuditRecord entities and saves them.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AuditPersistenceService {

    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For converting DTO to JSON string

    /**
     * Persists an audit record based on a UserEvent.
     *
     * @param userEvent         The UserEvent containing the event details.
     * @param sourceApplication Optional string to indicate the source of the event.
     */
    @Transactional // Ensure atomicity
    public void persistUserEventAudit(UserEvent userEvent, String sourceApplication) {
        if (userEvent == null || userEvent.userData() == null || userEvent.userData().id() == null) {
            log.warn("Attempted to persist an invalid user event. Event or its critical data is null. Event Type: {}",
                    userEvent != null ? userEvent.eventType() : "UNKNOWN");
            return; // Or throw an exception, depending on desired strictness
        }

        String eventDetailsJson = null;
        try {
            // Configure ObjectMapper if not already configured globally for JavaTimeModule
            // However, typically this is done at Spring Boot's Jackson auto-configuration level.
            // If UserDto contains Java Time types, ensure ObjectMapper can handle them.
            ObjectMapper localObjectMapper = objectMapper.copy(); // Use a copy to avoid modifying the shared bean's state
            if (!localObjectMapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getName())) {
                localObjectMapper.registerModule(new JavaTimeModule());
            }
            eventDetailsJson = localObjectMapper.writeValueAsString(userEvent.userData()); // Store only UserDto as JSON
        } catch (JsonProcessingException e) {
            log.error("Error serializing UserEvent.userData to JSON for audit. UserID: {}, EventType: {}. Error: {}",
                    userEvent.userData().id(), userEvent.eventType(), e.getMessage(), e);
            // Decide if you want to proceed with a null/placeholder eventDetails or throw an error
            eventDetailsJson = "{\"error\":\"Failed to serialize event details\"}";
        }

        AuditRecord auditRecord = AuditRecord.builder()
                .eventType(userEvent.eventType())
                .userId(userEvent.userData().id().toString())
                .eventDetails(eventDetailsJson)
                .sourceApplication(sourceApplication)
                // eventTimestamp is set by @CreationTimestamp
                .build();

        try {
            auditRepository.save(auditRecord);
            log.info("Successfully persisted audit record for UserID: {}, EventType: {}",
                    auditRecord.getUserId(), auditRecord.getEventType());
        } catch (Exception e) {
            log.error("Error saving audit record for UserID: {}, EventType: {}. Error: {}",
                    auditRecord.getUserId(), auditRecord.getEventType(), e.getMessage(), e);
            // Consider rethrowing or specific error handling
        }
    }

    /**
     * Overloaded method to persist an audit record without specifying a source application.
     *
     * @param userEvent The UserEvent containing the event details.
     */
    @Transactional
    public void persistUserEventAudit(UserEvent userEvent) {
        persistUserEventAudit(userEvent, null);
    }
}
