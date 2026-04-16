package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, UUID> {

    List<ExamSchedule> findByExamIdOrderByStartTimeAsc(UUID examId);

    @Query("SELECT s FROM ExamSchedule s WHERE s.exam.id = :examId AND s.startTime >= :from AND s.startTime <= :to ORDER BY s.startTime")
    List<ExamSchedule> findByExamIdAndDateRange(UUID examId, OffsetDateTime from, OffsetDateTime to);

    @Query("SELECT s FROM ExamSchedule s WHERE s.exam.tenant.id = :tenantId ORDER BY s.startTime DESC")
    List<ExamSchedule> findByTenantId(UUID tenantId);

    @Query("SELECT s FROM ExamSchedule s WHERE s.exam.tenant.id = :tenantId AND s.startTime >= :from AND s.startTime <= :to ORDER BY s.startTime")
    List<ExamSchedule> findByTenantIdAndDateRange(UUID tenantId, OffsetDateTime from, OffsetDateTime to);

    Optional<ExamSchedule> findByIdAndExamId(UUID id, UUID examId);
}
