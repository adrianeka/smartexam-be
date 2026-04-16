package com.tujuhsembilan.smartedutelu.domain.support.repository;

import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketStatusRepository extends JpaRepository<TicketStatus, UUID> {
}
