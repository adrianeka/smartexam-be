package com.tujuhsembilan.smartedutelu.domain.evaluation.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.GradeAnswerRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.AttemptAnswerResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.AttemptResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttempt;
import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttemptAnswer;
import com.tujuhsembilan.smartedutelu.domain.evaluation.repository.ExamAttemptAnswerRepository;
import com.tujuhsembilan.smartedutelu.domain.evaluation.repository.ExamAttemptRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradingService {

    private final ExamAttemptRepository attemptRepository;
    private final ExamAttemptAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final TenantUserRepository tenantUserRepository;

    @Transactional(readOnly = true)
    public Page<AttemptResponse> listPendingGrading(Pageable pageable) {
        return attemptRepository.findPendingGrading(pageable)
                .map(AttemptResponse::from);
    }

    @Transactional(readOnly = true)
    public AttemptResponse getAttemptDetail(UUID attemptId) {
        ExamAttempt attempt = attemptRepository.findByIdWithAnswers(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_001));
        validateTenantAccess(attempt);
        return AttemptResponse.fromWithAnswers(attempt);
    }

    @Transactional
    public AttemptAnswerResponse gradeAnswer(UUID attemptId, UUID answerId, GradeAnswerRequest request) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_001));

        validateTenantAccess(attempt);

        if (attempt.getScore() != null) {
            throw new BusinessException(ErrorCode.SE_GRD_003);
        }

        ExamAttemptAnswer answer = answerRepository.findByIdAndAttemptId(answerId, attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_002));

        answer.setScore(request.getScore());
        answerRepository.save(answer);

        log.info("Answer {} graded with score {} for attempt {}", answerId, request.getScore(), attemptId);
        return AttemptAnswerResponse.from(answer);
    }

    @Transactional
    public AttemptResponse finalizeAttempt(UUID attemptId) {
        ExamAttempt attempt = attemptRepository.findByIdWithAnswers(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_001));

        validateTenantAccess(attempt);

        if (attempt.getScore() != null) {
            throw new BusinessException(ErrorCode.SE_GRD_003);
        }

        BigDecimal totalScore = attempt.getAnswers().stream()
                .map(a -> a.getScore() != null ? a.getScore() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        attempt.setScore(totalScore);

        Integer passPercentageInt = attempt.getExam().getPassPercentage();
        BigDecimal passPercentage = passPercentageInt != null ? BigDecimal.valueOf(passPercentageInt) : null;
        BigDecimal examTotalScore = attempt.getExam().getTotalScore();
        if (passPercentage != null && examTotalScore != null && examTotalScore.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = totalScore.multiply(BigDecimal.valueOf(100)).divide(examTotalScore, 2, java.math.RoundingMode.HALF_UP);
            attempt.setPassed(pct.compareTo(passPercentage) >= 0);
        }

        attemptRepository.save(attempt);
        log.info("Attempt {} finalized with score {}", attemptId, totalScore);
        return AttemptResponse.fromWithAnswers(attempt);
    }

    @Transactional(readOnly = true)
    public Page<AttemptResponse> listResults(UUID examId, Pageable pageable) {
        if (examId != null) {
            return attemptRepository.findByExamId(examId, pageable)
                    .map(AttemptResponse::from);
        }
        return attemptRepository.findAll(pageable)
                .map(AttemptResponse::from);
    }

    @Transactional
    public AttemptResponse publishResult(UUID attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_001));

        if (attempt.getScore() == null) {
            throw new BusinessException(ErrorCode.SE_GRD_006);
        }

        log.info("Result published for attempt {}", attemptId);
        return AttemptResponse.from(attempt);
    }

    private void validateTenantAccess(ExamAttempt attempt) {
        UUID tenantId = attempt.getExam().getTenant().getId();
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        if (!tenantUserRepository.existsByTenantIdAndUserId(tenantId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses ke tenant ini");
        }
    }
}
