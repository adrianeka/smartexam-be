package com.tujuhsembilan.smartedutelu.domain.analytics.repository;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAppeal;
import com.tujuhsembilan.smartedutelu.domain.analytics.enums.AppealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExamAppealRepository extends JpaRepository<ExamAppeal, UUID> {

    Page<ExamAppeal> findByStatus(AppealStatus status, Pageable pageable);

    Page<ExamAppeal> findByUserId(UUID userId, Pageable pageable);
}
