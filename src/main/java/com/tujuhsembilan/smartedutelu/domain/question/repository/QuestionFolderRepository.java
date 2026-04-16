package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionFolderRepository extends JpaRepository<QuestionFolder, UUID> {

    List<QuestionFolder> findByTenantIdOrderByPositionAscNameAsc(UUID tenantId);

    @Query("SELECT f FROM QuestionFolder f WHERE f.tenant.id = :tenantId AND f.parent IS NULL ORDER BY f.position, f.name")
    List<QuestionFolder> findRootsByTenantId(UUID tenantId);

    Optional<QuestionFolder> findByIdAndTenantId(UUID id, UUID tenantId);
}
