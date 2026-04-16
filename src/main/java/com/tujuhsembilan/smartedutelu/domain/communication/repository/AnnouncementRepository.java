package com.tujuhsembilan.smartedutelu.domain.communication.repository;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    Page<Announcement> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    @Query("SELECT a FROM Announcement a LEFT JOIN FETCH a.attachments WHERE a.id = :id")
    Optional<Announcement> findByIdWithAttachments(@Param("id") UUID id);
}
