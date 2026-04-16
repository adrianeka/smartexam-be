package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, UUID> {

    List<QuestionCategory> findByTenantIdOrderByNameAsc(UUID tenantId);

    Optional<QuestionCategory> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByNameAndTenantId(String name, UUID tenantId);

    boolean existsByNameAndTenantIdAndIdNot(String name, UUID tenantId, UUID excludeId);
}
