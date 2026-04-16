package com.tujuhsembilan.smartedutelu.domain.communication.repository;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender LEFT JOIN FETCH m.receiver WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.sentAt DESC")
    Page<Message> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.attachments WHERE m.id = :id")
    Optional<Message> findByIdWithAttachments(@Param("id") UUID id);
}
