package com.tujuhsembilan.smartedutelu.domain.analytics.repository;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamAnalyticsRepository extends JpaRepository<ExamAnalytics, UUID> {

    Optional<ExamAnalytics> findByExamId(UUID examId);
}
