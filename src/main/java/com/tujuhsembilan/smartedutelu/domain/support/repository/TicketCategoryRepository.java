package com.tujuhsembilan.smartedutelu.domain.support.repository;

import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, UUID> {
}
