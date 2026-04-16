package com.tujuhsembilan.smartedutelu.domain.support.repository;

import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketPriorityRepository extends JpaRepository<TicketPriority, UUID> {
}
