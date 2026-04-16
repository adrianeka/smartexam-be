package com.tujuhsembilan.smartedutelu.domain.logging.repository;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    Page<ActivityLog> findByUserId(UUID userId, Pageable pageable);

    Page<ActivityLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
}
