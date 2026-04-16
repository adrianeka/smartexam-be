package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, UUID> {

    List<ExamRoom> findByTenantIdAndIsActiveTrueOrderByNameAsc(UUID tenantId);

    Optional<ExamRoom> findByIdAndTenantId(UUID id, UUID tenantId);
}
