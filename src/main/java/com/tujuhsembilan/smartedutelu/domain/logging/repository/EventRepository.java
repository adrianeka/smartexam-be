package com.tujuhsembilan.smartedutelu.domain.logging.repository;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByEventType(String eventType, Pageable pageable);

    Page<Event> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    Page<Event> findByUserId(UUID userId, Pageable pageable);
}
