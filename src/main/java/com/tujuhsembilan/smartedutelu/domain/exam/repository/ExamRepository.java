package com.tujuhsembilan.smartedutelu.domain.exam.repository;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID>, JpaSpecificationExecutor<Exam> {

    @Override
    @EntityGraph(attributePaths = {"tenant", "category", "createdBy"})
    Page<Exam> findAll(Specification<Exam> spec, Pageable pageable);

    @Query("SELECT e FROM Exam e " +
           "LEFT JOIN FETCH e.sections s " +
           "LEFT JOIN FETCH s.examQuestions " +
           "WHERE e.id = :id AND e.tenant.id = :tenantId")
    Optional<Exam> findByIdAndTenantIdWithDetails(UUID id, UUID tenantId);

    Optional<Exam> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsBySlugAndTenantId(String slug, UUID tenantId);

    boolean existsBySlugAndTenantIdAndIdNot(String slug, UUID tenantId, UUID excludeId);
}
