package com.tujuhsembilan.smartedutelu.domain.evaluation.repository;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.GradingRubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradingRubricRepository extends JpaRepository<GradingRubric, UUID> {

    List<GradingRubric> findByQuestionId(UUID questionId);

    @Query("SELECT DISTINCT r FROM GradingRubric r LEFT JOIN FETCH r.criteria WHERE r.question.id = :questionId")
    List<GradingRubric> findByQuestionIdWithCriteria(@Param("questionId") UUID questionId);

    @Query("SELECT r FROM GradingRubric r LEFT JOIN FETCH r.criteria WHERE r.id = :id")
    Optional<GradingRubric> findByIdWithCriteria(@Param("id") UUID id);
}
