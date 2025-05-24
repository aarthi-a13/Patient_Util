package com.health.util.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents an audit record for user events.
 * This entity is persisted to the database to log user-related activities.
 */
@Entity
@Table(name = "user_audit_log") // Explicit table name
@Data // Lombok: @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor // JPA requirement
@AllArgsConstructor // For builder
@Builder
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // Automatically set by Hibernate on creation
    @Column(nullable = false, updatable = false)
    private LocalDateTime eventTimestamp;

    @Column(nullable = false)
    private String eventType; // e.g., USER_CREATED, USER_UPDATED

    @Column(nullable = false)
    private String userId; // The ID of the user the event pertains to

    @Column(columnDefinition = "TEXT") // For potentially large JSON strings
    private String eventDetails; // JSON string of UserEventDto or just UserDto

    private String sourceApplication; // Optional: To track where the event originated if multiple apps use this library
}
