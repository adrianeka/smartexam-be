package com.tujuhsembilan.smartedutelu.domain.exam.service;

import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamQuestionResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.SectionResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamCategory;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamQuestion;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamSection;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.*;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.question.entity.Question;
import com.tujuhsembilan.smartedutelu.domain.question.repository.QuestionRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSectionRepository sectionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamCategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    // ══════════════════════════════════════════════════════════════════════════════
    //  EXAM CRUD
    // ══════════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> listExams(UUID tenantId, String status, String examType,
                                                 UUID categoryId, String keyword, Pageable pageable) {
        Specification<Exam> spec = Specification.where(ExamSpecification.hasTenant(tenantId));

        if (status != null) spec = spec.and(ExamSpecification.hasStatus(status));
        if (examType != null) spec = spec.and(ExamSpecification.hasExamType(examType));
        if (categoryId != null) spec = spec.and(ExamSpecification.hasCategory(categoryId));
        if (keyword != null && !keyword.isBlank()) spec = spec.and(ExamSpecification.searchKeyword(keyword));

        Page<ExamResponse> page = examRepository.findAll(spec, pageable)
                .map(this::toExamSummary);

        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public ExamResponse getById(UUID tenantId, UUID id) {
        Exam exam = examRepository.findByIdAndTenantIdWithDetails(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001));
        return toExamFull(exam);
    }

    @Transactional
    public ExamResponse create(CreateExamRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        if (examRepository.existsBySlugAndTenantId(request.getSlug(), request.getTenantId())) {
            throw new DuplicateResourceException(ErrorCode.SE_EXM_003);
        }

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User creator = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        ExamCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndTenantId(request.getCategoryId(), request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAT_001));
        }

        Exam exam = Exam.builder()
                .tenant(tenant)
                .category(category)
                .title(request.getTitle())
                .slug(request.getSlug())
                .description(request.getDescription())
                .examType(request.getExamType() != null ? request.getExamType() : "standard")
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .maxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1)
                .passPercentage(request.getPassPercentage() != null ? request.getPassPercentage() : 60)
                .totalScore(request.getTotalScore() != null ? request.getTotalScore() : new BigDecimal("100"))
                .randomQuestions(request.getRandomQuestions() != null ? request.getRandomQuestions() : false)
                .randomAnswers(request.getRandomAnswers() != null ? request.getRandomAnswers() : false)
                .showResultMode(request.getShowResultMode() != null ? request.getShowResultMode() : "after_submit")
                .allowReview(request.getAllowReview() != null ? request.getAllowReview() : true)
                .shuffleSections(request.getShuffleSections() != null ? request.getShuffleSections() : false)
                .requireProctoring(request.getRequireProctoring() != null ? request.getRequireProctoring() : false)
                .feedbackType(request.getFeedbackType() != null ? request.getFeedbackType() : "summary")
                .instructions(request.getInstructions())
                .status("draft")
                .createdBy(creator)
                .build();

        exam = examRepository.save(exam);
        log.info("Created exam: {} [tenant={}]", exam.getTitle(), tenant.getName());
        return toExamSummary(exam);
    }

    @Transactional
    public ExamResponse update(UUID tenantId, UUID id, UpdateExamRequest request) {
        Exam exam = findExamOrThrow(tenantId, id);
        assertDraft(exam);

        if (request.getSlug() != null) {
            if (examRepository.existsBySlugAndTenantIdAndIdNot(request.getSlug(), tenantId, id)) {
                throw new DuplicateResourceException(ErrorCode.SE_EXM_003);
            }
            exam.setSlug(request.getSlug());
        }
        if (request.getCategoryId() != null) {
            ExamCategory category = categoryRepository.findByIdAndTenantId(request.getCategoryId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAT_001));
            exam.setCategory(category);
        }
        if (request.getTitle() != null) exam.setTitle(request.getTitle());
        if (request.getDescription() != null) exam.setDescription(request.getDescription());
        if (request.getExamType() != null) exam.setExamType(request.getExamType());
        if (request.getTimeLimitMinutes() != null) exam.setTimeLimitMinutes(request.getTimeLimitMinutes());
        if (request.getMaxAttempts() != null) exam.setMaxAttempts(request.getMaxAttempts());
        if (request.getPassPercentage() != null) exam.setPassPercentage(request.getPassPercentage());
        if (request.getTotalScore() != null) exam.setTotalScore(request.getTotalScore());
        if (request.getRandomQuestions() != null) exam.setRandomQuestions(request.getRandomQuestions());
        if (request.getRandomAnswers() != null) exam.setRandomAnswers(request.getRandomAnswers());
        if (request.getShowResultMode() != null) exam.setShowResultMode(request.getShowResultMode());
        if (request.getAllowReview() != null) exam.setAllowReview(request.getAllowReview());
        if (request.getShuffleSections() != null) exam.setShuffleSections(request.getShuffleSections());
        if (request.getRequireProctoring() != null) exam.setRequireProctoring(request.getRequireProctoring());
        if (request.getFeedbackType() != null) exam.setFeedbackType(request.getFeedbackType());
        if (request.getInstructions() != null) exam.setInstructions(request.getInstructions());

        exam = examRepository.save(exam);
        log.info("Updated exam: {}", exam.getTitle());
        return toExamSummary(exam);
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Exam exam = findExamOrThrow(tenantId, id);
        exam.setDeletedAt(OffsetDateTime.now());
        examRepository.save(exam);
        log.info("Soft-deleted exam: {}", id);
    }

    @Transactional
    public ExamResponse publish(UUID tenantId, UUID id) {
        Exam exam = findExamOrThrow(tenantId, id);
        if (!"draft".equals(exam.getStatus())) {
            throw new BusinessException(ErrorCode.SE_EXM_007);
        }
        exam.setStatus("published");
        exam = examRepository.save(exam);
        log.info("Published exam: {}", exam.getTitle());
        return toExamSummary(exam);
    }

    @Transactional
    public ExamResponse archive(UUID tenantId, UUID id) {
        Exam exam = findExamOrThrow(tenantId, id);
        if ("archived".equals(exam.getStatus())) {
            throw new BusinessException(ErrorCode.SE_EXM_007);
        }
        exam.setStatus("archived");
        exam = examRepository.save(exam);
        log.info("Archived exam: {}", exam.getTitle());
        return toExamSummary(exam);
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  SECTIONS
    // ══════════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<SectionResponse> listSections(UUID tenantId, UUID examId) {
        findExamOrThrow(tenantId, examId);
        return sectionRepository.findByExamIdOrderByPositionAsc(examId).stream()
                .map(this::toSectionResponse)
                .toList();
    }

    @Transactional
    public SectionResponse createSection(UUID tenantId, UUID examId, CreateSectionRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        ExamSection section = ExamSection.builder()
                .exam(exam)
                .title(request.getTitle())
                .instruction(request.getInstruction())
                .position(request.getPosition() != null ? request.getPosition() : exam.getSections().size())
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .build();

        section = sectionRepository.save(section);
        log.info("Created section '{}' in exam {}", section.getTitle(), examId);
        return toSectionResponse(section);
    }

    @Transactional
    public SectionResponse updateSection(UUID tenantId, UUID examId, UUID sectionId, UpdateSectionRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        ExamSection section = sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        if (request.getTitle() != null) section.setTitle(request.getTitle());
        if (request.getInstruction() != null) section.setInstruction(request.getInstruction());
        if (request.getPosition() != null) section.setPosition(request.getPosition());
        if (request.getTimeLimitSeconds() != null) section.setTimeLimitSeconds(request.getTimeLimitSeconds());

        section = sectionRepository.save(section);
        log.info("Updated section '{}' in exam {}", section.getTitle(), examId);
        return toSectionResponse(section);
    }

    @Transactional
    public void deleteSection(UUID tenantId, UUID examId, UUID sectionId) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        ExamSection section = sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        sectionRepository.delete(section);
        log.info("Deleted section {} from exam {}", sectionId, examId);
    }

    @Transactional
    public List<SectionResponse> reorderSections(UUID tenantId, UUID examId, ReorderRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        List<ExamSection> sections = sectionRepository.findByExamIdOrderByPositionAsc(examId);
        Map<UUID, ExamSection> sectionMap = sections.stream()
                .collect(Collectors.toMap(ExamSection::getId, Function.identity()));

        int pos = 0;
        for (UUID sectionId : request.getOrderedIds()) {
            ExamSection section = sectionMap.get(sectionId);
            if (section != null) {
                section.setPosition(pos++);
            }
        }
        sectionRepository.saveAll(sections);
        log.info("Reordered {} sections in exam {}", sections.size(), examId);

        return sectionRepository.findByExamIdOrderByPositionAsc(examId).stream()
                .map(this::toSectionResponse)
                .toList();
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  EXAM QUESTIONS (inside sections)
    // ══════════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<ExamQuestionResponse> listSectionQuestions(UUID tenantId, UUID examId, UUID sectionId) {
        findExamOrThrow(tenantId, examId);
        sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        return examQuestionRepository.findBySectionIdWithQuestion(sectionId).stream()
                .map(this::toExamQuestionResponse)
                .toList();
    }

    @Transactional
    public List<ExamQuestionResponse> addQuestionsToSection(UUID tenantId, UUID examId, UUID sectionId,
                                                             AddQuestionsToSectionRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        ExamSection section = sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        int currentSize = section.getExamQuestions().size();
        int pos = currentSize;

        for (AddQuestionsToSectionRequest.QuestionItem item : request.getQuestions()) {
            if (examQuestionRepository.existsBySectionIdAndQuestionId(sectionId, item.getQuestionId())) {
                throw new DuplicateResourceException(ErrorCode.SE_EXM_006);
            }

            Question question = questionRepository.findById(item.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

            ExamQuestion eq = ExamQuestion.builder()
                    .section(section)
                    .question(question)
                    .position(pos++)
                    .weight(item.getWeight() != null ? item.getWeight() : BigDecimal.ONE)
                    .build();

            examQuestionRepository.save(eq);
        }

        // Update question count
        section.setQuestionCount(currentSize + request.getQuestions().size());
        sectionRepository.save(section);

        log.info("Added {} questions to section {} in exam {}", request.getQuestions().size(), sectionId, examId);

        return examQuestionRepository.findBySectionIdWithQuestion(sectionId).stream()
                .map(this::toExamQuestionResponse)
                .toList();
    }

    @Transactional
    public ExamQuestionResponse updateExamQuestion(UUID tenantId, UUID examId, UUID sectionId,
                                                    UUID eqId, UpdateExamQuestionRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        ExamQuestion eq = examQuestionRepository.findByIdAndSectionId(eqId, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_005));

        if (request.getWeight() != null) eq.setWeight(request.getWeight());
        if (request.getPosition() != null) eq.setPosition(request.getPosition());

        eq = examQuestionRepository.save(eq);
        log.info("Updated exam question {} in section {}", eqId, sectionId);
        return toExamQuestionResponse(eq);
    }

    @Transactional
    public void deleteExamQuestion(UUID tenantId, UUID examId, UUID sectionId, UUID eqId) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        ExamSection section = sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        ExamQuestion eq = examQuestionRepository.findByIdAndSectionId(eqId, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_005));

        examQuestionRepository.delete(eq);
        section.setQuestionCount(Math.max(0, section.getQuestionCount() - 1));
        sectionRepository.save(section);
        log.info("Removed exam question {} from section {}", eqId, sectionId);
    }

    @Transactional
    public List<ExamQuestionResponse> reorderSectionQuestions(UUID tenantId, UUID examId, UUID sectionId,
                                                              ReorderRequest request) {
        Exam exam = findExamOrThrow(tenantId, examId);
        assertDraft(exam);

        sectionRepository.findByIdAndExamId(sectionId, examId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_004));

        List<ExamQuestion> questions = examQuestionRepository.findBySectionIdWithQuestion(sectionId);
        Map<UUID, ExamQuestion> eqMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestion::getId, Function.identity()));

        int pos = 0;
        for (UUID eqId : request.getOrderedIds()) {
            ExamQuestion eq = eqMap.get(eqId);
            if (eq != null) {
                eq.setPosition(pos++);
            }
        }
        examQuestionRepository.saveAll(questions);
        log.info("Reordered {} questions in section {}", questions.size(), sectionId);

        return examQuestionRepository.findBySectionIdWithQuestion(sectionId).stream()
                .map(this::toExamQuestionResponse)
                .toList();
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════════

    private Exam findExamOrThrow(UUID tenantId, UUID id) {
        return examRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001));
    }

    private void assertDraft(Exam exam) {
        if (!"draft".equals(exam.getStatus())) {
            throw new BusinessException(ErrorCode.SE_EXM_008);
        }
    }

    private ExamResponse toExamSummary(Exam e) {
        return ExamResponse.builder()
                .id(e.getId())
                .tenantId(e.getTenant().getId())
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
                .title(e.getTitle())
                .slug(e.getSlug())
                .description(e.getDescription())
                .examType(e.getExamType())
                .timeLimitMinutes(e.getTimeLimitMinutes())
                .maxAttempts(e.getMaxAttempts())
                .passPercentage(e.getPassPercentage())
                .totalScore(e.getTotalScore())
                .randomQuestions(e.getRandomQuestions())
                .randomAnswers(e.getRandomAnswers())
                .showResultMode(e.getShowResultMode())
                .allowReview(e.getAllowReview())
                .shuffleSections(e.getShuffleSections())
                .requireProctoring(e.getRequireProctoring())
                .feedbackType(e.getFeedbackType())
                .instructions(e.getInstructions())
                .status(e.getStatus())
                .createdById(e.getCreatedBy().getId())
                .createdByName(e.getCreatedBy().getName())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private ExamResponse toExamFull(Exam e) {
        ExamResponse response = toExamSummary(e);
        if (e.getSections() != null) {
            response.setSections(e.getSections().stream()
                    .map(s -> {
                        SectionResponse sr = toSectionResponse(s);
                        if (s.getExamQuestions() != null) {
                            sr.setQuestions(s.getExamQuestions().stream()
                                    .map(this::toExamQuestionResponse)
                                    .toList());
                        }
                        return sr;
                    })
                    .toList());
        }
        return response;
    }

    private SectionResponse toSectionResponse(ExamSection s) {
        return SectionResponse.builder()
                .id(s.getId())
                .examId(s.getExam().getId())
                .title(s.getTitle())
                .instruction(s.getInstruction())
                .position(s.getPosition())
                .timeLimitSeconds(s.getTimeLimitSeconds())
                .questionCount(s.getQuestionCount())
                .build();
    }

    private ExamQuestionResponse toExamQuestionResponse(ExamQuestion eq) {
        return ExamQuestionResponse.builder()
                .id(eq.getId())
                .questionId(eq.getQuestion().getId())
                .questionText(eq.getQuestion().getQuestionText())
                .questionType(eq.getQuestion().getType())
                .difficultyLevel(eq.getQuestion().getDifficultyLevel())
                .position(eq.getPosition())
                .weight(eq.getWeight())
                .build();
    }
}
