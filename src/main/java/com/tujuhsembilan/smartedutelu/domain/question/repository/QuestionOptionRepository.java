package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {

    Optional<QuestionOption> findByIdAndQuestionId(UUID id, UUID questionId);
}
