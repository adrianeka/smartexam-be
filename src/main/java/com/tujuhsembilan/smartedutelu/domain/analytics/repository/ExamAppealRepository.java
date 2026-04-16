package com.tujuhsembilan.smartedutelu.domain.analytics.repository;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAppeal;
import com.tujuhsembilan.smartedutelu.domain.analytics.enums.AppealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExamAppealRepository extends JpaRepository<ExamAppeal, UUID> {

    @Query("SELECT a FROM ExamAppeal a LEFT JOIN FETCH a.result LEFT JOIN FETCH a.user LEFT JOIN FETCH a.resolvedBy WHERE a.status = :status")
    Page<ExamAppeal> findByStatus(@Param("status") AppealStatus status, Pageable pageable);

    @Query("SELECT a FROM ExamAppeal a LEFT JOIN FETCH a.result LEFT JOIN FETCH a.user LEFT JOIN FETCH a.resolvedBy WHERE a.user.id = :userId")
    Page<ExamAppeal> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT a FROM ExamAppeal a LEFT JOIN FETCH a.result LEFT JOIN FETCH a.user LEFT JOIN FETCH a.resolvedBy")
    Page<ExamAppeal> findAllWithRelations(Pageable pageable);
}
