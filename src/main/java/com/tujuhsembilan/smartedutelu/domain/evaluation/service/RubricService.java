package com.tujuhsembilan.smartedutelu.domain.evaluation.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.CreateCriteriaRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.CreateRubricRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.UpdateCriteriaRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.UpdateRubricRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.CriteriaResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.RubricResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.GradingRubric;
import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.RubricCriteria;
import com.tujuhsembilan.smartedutelu.domain.evaluation.repository.GradingRubricRepository;
import com.tujuhsembilan.smartedutelu.domain.evaluation.repository.RubricCriteriaRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.question.entity.Question;
import com.tujuhsembilan.smartedutelu.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class RubricService {

    private final GradingRubricRepository rubricRepository;
    private final RubricCriteriaRepository criteriaRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RubricResponse> getRubricsByQuestion(UUID questionId) {
        return rubricRepository.findByQuestionId(questionId).stream()
                .map(RubricResponse::from)
                .toList();
    }

    @Transactional
    public RubricResponse createRubric(CreateRubricRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User creator = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        GradingRubric rubric = GradingRubric.builder()
                .question(question)
                .title(request.getTitle())
                .description(request.getDescription())
                .maxScore(request.getMaxScore())
                .createdBy(creator)
                .build();

        if (request.getCriteria() != null && !request.getCriteria().isEmpty()) {
            AtomicInteger pos = new AtomicInteger(0);
            request.getCriteria().forEach(c -> {
                RubricCriteria criteria = RubricCriteria.builder()
                        .rubric(rubric)
                        .criterion(c.getCriterion())
                        .description(c.getDescription())
                        .maxScore(c.getMaxScore())
                        .position(pos.getAndIncrement())
                        .build();
                rubric.getCriteria().add(criteria);
            });
        }

        GradingRubric saved = rubricRepository.save(rubric);
        log.info("Rubric created: {} for question: {}", saved.getId(), question.getId());
        return RubricResponse.from(saved);
    }

    @Transactional
    public RubricResponse updateRubric(UUID rubricId, UpdateRubricRequest request) {
        GradingRubric rubric = rubricRepository.findByIdWithCriteria(rubricId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_004));

        if (request.getTitle() != null) rubric.setTitle(request.getTitle());
        if (request.getDescription() != null) rubric.setDescription(request.getDescription());
        if (request.getMaxScore() != null) rubric.setMaxScore(request.getMaxScore());

        return RubricResponse.from(rubricRepository.save(rubric));
    }

    @Transactional
    public void deleteRubric(UUID rubricId) {
        GradingRubric rubric = rubricRepository.findById(rubricId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_004));
        rubricRepository.delete(rubric);
        log.info("Rubric deleted: {}", rubricId);
    }

    @Transactional
    public CriteriaResponse addCriteria(UUID rubricId, CreateCriteriaRequest request) {
        GradingRubric rubric = rubricRepository.findByIdWithCriteria(rubricId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_004));

        int nextPosition = rubric.getCriteria().size();

        RubricCriteria criteria = RubricCriteria.builder()
                .rubric(rubric)
                .criterion(request.getCriterion())
                .description(request.getDescription())
                .maxScore(request.getMaxScore())
                .position(nextPosition)
                .build();

        RubricCriteria saved = criteriaRepository.save(criteria);
        log.info("Criteria added: {} to rubric: {}", saved.getId(), rubricId);
        return CriteriaResponse.from(saved);
    }

    @Transactional
    public CriteriaResponse updateCriteria(UUID rubricId, UUID criteriaId, UpdateCriteriaRequest request) {
        RubricCriteria criteria = criteriaRepository.findByIdAndRubricId(criteriaId, rubricId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_005));

        if (request.getCriterion() != null) criteria.setCriterion(request.getCriterion());
        if (request.getDescription() != null) criteria.setDescription(request.getDescription());
        if (request.getMaxScore() != null) criteria.setMaxScore(request.getMaxScore());

        return CriteriaResponse.from(criteriaRepository.save(criteria));
    }

    @Transactional
    public void deleteCriteria(UUID rubricId, UUID criteriaId) {
        RubricCriteria criteria = criteriaRepository.findByIdAndRubricId(criteriaId, rubricId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_GRD_005));
        criteriaRepository.delete(criteria);
        log.info("Criteria deleted: {} from rubric: {}", criteriaId, rubricId);
    }
}
