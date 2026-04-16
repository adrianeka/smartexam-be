package com.tujuhsembilan.smartedutelu.domain.evaluation.repository;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamAttemptAnswerRepository extends JpaRepository<ExamAttemptAnswer, UUID> {

    List<ExamAttemptAnswer> findByAttemptId(UUID attemptId);

    Optional<ExamAttemptAnswer> findByIdAndAttemptId(UUID id, UUID attemptId);
}
