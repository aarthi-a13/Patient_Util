package com.health.util.audit.repository;

import com.health.util.audit.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for {@link AuditRecord} entities.
 * Provides methods to perform database operations on audit records.
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditRecord, Long> {
    // JpaRepository provides common methods like save(), findById(), findAll(), etc.
    // Custom query methods can be added here if needed, for example:
    List<AuditRecord> findByEventType(String eventType);

    List<AuditRecord> findByUserId(String userId);

    List<AuditRecord> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end);
}
