package com.tujuhsembilan.smartedutelu.domain.analytics.repository;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ExamResultRepository extends JpaRepository<ExamResult, UUID> {

    Page<ExamResult> findByExamId(UUID examId, Pageable pageable);

    Page<ExamResult> findByUserId(UUID userId, Pageable pageable);

    Optional<ExamResult> findByAttemptId(UUID attemptId);

    Optional<ExamResult> findByUserIdAndExamId(UUID userId, UUID examId);

    long countByExamIdAndIsPassed(UUID examId, boolean isPassed);

    long countByExamId(UUID examId);

    @Query("SELECT COUNT(r) FROM ExamResult r WHERE r.exam.id = :examId AND r.publishedAt IS NOT NULL")
    long countCompletionsByExamId(@Param("examId") UUID examId);

    @Query("SELECT AVG(r.totalScore) FROM ExamResult r WHERE r.exam.id = :examId")
    BigDecimal avgScoreByExamId(@Param("examId") UUID examId);
}
