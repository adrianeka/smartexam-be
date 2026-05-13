package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {

    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN FETCH q.options " +
            "LEFT JOIN FETCH q.attachments " +
            "WHERE q.id = :id AND q.tenant.id = :tenantId")
    Optional<Question> findByIdAndTenantIdWithDetails(@Param("id") UUID id,
            @Param("tenantId") UUID tenantId);

    @Query("SELECT q FROM Question q WHERE q.id = :id AND q.tenant.id = :tenantId")
    Optional<Question> findByIdAndTenantId(@Param("id") UUID id,
            @Param("tenantId") UUID tenantId);
}
