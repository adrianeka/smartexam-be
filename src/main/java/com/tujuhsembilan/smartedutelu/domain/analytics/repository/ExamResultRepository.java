package com.tujuhsembilan.smartedutelu.domain.analytics.repository;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamResultRepository extends JpaRepository<ExamResult, UUID> {

    Page<ExamResult> findByExamId(UUID examId, Pageable pageable);

    Page<ExamResult> findByUserId(UUID userId, Pageable pageable);

    Optional<ExamResult> findByAttemptId(UUID attemptId);

    long countByExamIdAndIsPassed(UUID examId, boolean isPassed);

    long countByExamId(UUID examId);
}
