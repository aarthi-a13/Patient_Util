package com.health.util.event;

/**
 * Represents an event related to a user, to be sent via Kafka.
 * This record carries the event type and the user data associated with the event.
 *
 * @param eventType The type of event (e.g., CREATED, UPDATED, DELETED).
 * @param userData The user data associated with the event.
 */
public record UserEvent(
        String eventType,
        User userData
) {
}