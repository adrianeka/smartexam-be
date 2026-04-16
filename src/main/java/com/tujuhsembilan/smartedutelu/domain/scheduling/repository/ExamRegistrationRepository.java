package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, UUID> {

    Page<ExamRegistration> findByExamId(UUID examId, Pageable pageable);

    Page<ExamRegistration> findByUserId(UUID userId, Pageable pageable);

    boolean existsByExamIdAndUserIdAndScheduleId(UUID examId, UUID userId, UUID scheduleId);

    Optional<ExamRegistration> findByIdAndExamId(UUID id, UUID examId);
}
