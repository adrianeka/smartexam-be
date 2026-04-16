package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ProctorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProctorAssignmentRepository extends JpaRepository<ProctorAssignment, UUID> {

    List<ProctorAssignment> findByProctorIdOrderByAssignedAtDesc(UUID proctorId);

    boolean existsBySessionIdAndProctorId(UUID sessionId, UUID proctorId);
}
