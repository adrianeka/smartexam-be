package com.tujuhsembilan.smartedutelu.domain.analytics.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.AnalyticsResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAnalytics;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamAnalyticsRepository;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamResultRepository;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ExamAnalyticsRepository analyticsRepository;
    private final ExamResultRepository resultRepository;
    private final ExamRepository examRepository;

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(UUID examId) {
        ExamAnalytics analytics = analyticsRepository.findByExamId(examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_RES_005));
        return AnalyticsResponse.from(analytics);
    }

    @Transactional
    public AnalyticsResponse recalculate(UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001));

        long totalParticipants = resultRepository.countByExamId(examId);
        long totalPassed = resultRepository.countByExamIdAndIsPassed(examId, true);

        BigDecimal passRate = totalParticipants > 0
                ? BigDecimal.valueOf(totalPassed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalParticipants), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        ExamAnalytics analytics = analyticsRepository.findByExamId(examId)
                .orElse(ExamAnalytics.builder().exam(exam).build());

        analytics.setTotalParticipants((int) totalParticipants);
        analytics.setTotalCompletions((int) totalParticipants);
        analytics.setPassRate(passRate);
        analytics.setCalculatedAt(OffsetDateTime.now());

        analyticsRepository.save(analytics);
        log.info("Analytics recalculated for exam {}: {} participants, {}% pass rate",
                examId, totalParticipants, passRate);
        return AnalyticsResponse.from(analytics);
    }
}
