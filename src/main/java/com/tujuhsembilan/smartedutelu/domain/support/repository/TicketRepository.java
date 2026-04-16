package com.tujuhsembilan.smartedutelu.domain.support.repository;

import com.tujuhsembilan.smartedutelu.domain.support.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Page<Ticket> findByUserId(UUID userId, Pageable pageable);

    Page<Ticket> findByAssignedToId(UUID assignedToId, Pageable pageable);

    Page<Ticket> findByStatusId(UUID statusId, Pageable pageable);

    Optional<Ticket> findByCode(String code);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.messages WHERE t.id = :id")
    Optional<Ticket> findByIdWithMessages(@Param("id") UUID id);
}
