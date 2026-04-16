package com.tujuhsembilan.smartedutelu.domain.evaluation.repository;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {

    Page<ExamAttempt> findByExamId(UUID examId, Pageable pageable);

    Page<ExamAttempt> findByStudentId(UUID studentId, Pageable pageable);

    @Query("SELECT a FROM ExamAttempt a WHERE a.submittedAt IS NOT NULL AND a.score IS NULL")
    Page<ExamAttempt> findPendingGrading(Pageable pageable);

    @Query("SELECT a FROM ExamAttempt a LEFT JOIN FETCH a.answers WHERE a.id = :id")
    Optional<ExamAttempt> findByIdWithAnswers(@Param("id") UUID id);
}
