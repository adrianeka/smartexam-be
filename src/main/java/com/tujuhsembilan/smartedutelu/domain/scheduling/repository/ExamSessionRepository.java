package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamSession;
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

    @Query("SELECT s FROM ExamSession s WHERE s.endTime IS NULL AND s.exam.tenant.id = :tenantId ORDER BY s.startTime DESC")
    List<ExamSession> findActiveSessions(UUID tenantId);

    List<ExamSession> findByExamIdOrderByStartTimeDesc(UUID examId);
}
