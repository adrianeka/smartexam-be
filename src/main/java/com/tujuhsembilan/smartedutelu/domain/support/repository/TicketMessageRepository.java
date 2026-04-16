package com.tujuhsembilan.smartedutelu.domain.support.repository;

import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, UUID> {

    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
