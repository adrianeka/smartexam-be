package com.tujuhsembilan.smartedutelu.domain.exam.repository;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamCategoryRepository extends JpaRepository<ExamCategory, UUID> {

    // Root categories (no parent) for a given tenant
    @Query("SELECT c FROM ExamCategory c WHERE c.tenant.id = :tenantId AND c.parent IS NULL ORDER BY c.position, c.name")
    List<ExamCategory> findRootsByTenantId(UUID tenantId);

    // All categories for a tenant (for flat list or tree building in-memory)
    List<ExamCategory> findByTenantIdOrderByPositionAscNameAsc(UUID tenantId);

    Optional<ExamCategory> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsBySlugAndTenantId(String slug, UUID tenantId);

    boolean existsBySlugAndTenantIdAndIdNot(String slug, UUID tenantId, UUID excludeId);

    List<ExamCategory> findByParentIdOrderByPositionAscNameAsc(UUID parentId);
}
