package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {

    @Query("SELECT s FROM ExamSession s LEFT JOIN FETCH s.cheatingLogs WHERE s.id = :id")
    Optional<ExamSession> findByIdWithDetails(UUID id);

    @Query("SELECT s FROM ExamSession s LEFT JOIN FETCH s.cheatingLogs WHERE s.id = :id AND s.exam.tenant.id = :tenantId")
    Optional<ExamSession> findByIdAndTenantIdWithDetails(UUID id, UUID tenantId);

    @Query("SELECT s FROM ExamSession s WHERE s.endTime IS NULL AND s.exam.tenant.id = :tenantId ORDER BY s.startTime DESC")
    Page<ExamSession> findActiveSessions(UUID tenantId, Pageable pageable);

    List<ExamSession> findByExamIdOrderByStartTimeDesc(UUID examId);
}
