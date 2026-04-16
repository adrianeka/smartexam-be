package com.tujuhsembilan.smartedutelu.domain.evaluation.repository;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.RubricCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RubricCriteriaRepository extends JpaRepository<RubricCriteria, UUID> {

    Optional<RubricCriteria> findByIdAndRubricId(UUID id, UUID rubricId);
}
