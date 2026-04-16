package com.tujuhsembilan.smartedutelu.domain.question.service;

import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.media.dto.response.MediaResponse;
import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
import com.tujuhsembilan.smartedutelu.domain.media.repository.MediaFileRepository;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.AttachmentResponse;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.OptionResponse;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.QuestionResponse;
import com.tujuhsembilan.smartedutelu.domain.question.entity.*;
import com.tujuhsembilan.smartedutelu.domain.question.repository.*;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final QuestionAttachmentRepository attachmentRepository;
    private final QuestionCategoryRepository categoryRepository;
    private final QuestionFolderRepository folderRepository;
    private final QuestionMediaRepository questionMediaRepository;
    private final MediaFileRepository mediaFileRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    // ── List / Detail ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> listQuestions(UUID tenantId, String type, String difficulty,
                                                        UUID categoryId, UUID folderId, String keyword,
                                                        Pageable pageable) {
        Specification<Question> spec = Specification.where(QuestionSpecification.hasTenant(tenantId));

        if (type != null) spec = spec.and(QuestionSpecification.hasType(type));
        if (difficulty != null) spec = spec.and(QuestionSpecification.hasDifficulty(difficulty));
        if (categoryId != null) spec = spec.and(QuestionSpecification.hasCategory(categoryId));
        if (folderId != null) spec = spec.and(QuestionSpecification.hasFolder(folderId));
        if (keyword != null && !keyword.isBlank()) spec = spec.and(QuestionSpecification.searchKeyword(keyword));

        Page<QuestionResponse> page = questionRepository.findAll(spec, pageable)
                .map(this::toResponseSummary);

        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public QuestionResponse getById(UUID tenantId, UUID id) {
        Question question = questionRepository.findByIdAndTenantIdWithDetails(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));
        return toResponseFull(question);
    }

    // ── Question CRUD ───────────────────────────────────────────────────────────

    @Transactional
    public QuestionResponse create(CreateQuestionRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User creator = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        QuestionCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndTenantId(request.getCategoryId(), request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_005));
        }

        QuestionFolder folder = null;
        if (request.getFolderId() != null) {
            folder = folderRepository.findByIdAndTenantId(request.getFolderId(), request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_004));
        }

        Question question = Question.builder()
                .tenant(tenant)
                .category(category)
                .folder(folder)
                .questionText(request.getQuestionText())
                .description(request.getDescription())
                .explanation(request.getExplanation())
                .type(request.getType() != null ? request.getType() : "multiple_choice")
                .points(request.getPoints() != null ? request.getPoints() : 1)
                .difficultyLevel(request.getDifficultyLevel() != null ? request.getDifficultyLevel() : "medium")
                .timeEstimateSeconds(request.getTimeEstimateSeconds())
                .isShared(request.getIsShared() != null ? request.getIsShared() : false)
                .createdBy(creator)
                .build();

        // Inline options
        if (request.getOptions() != null) {
            int pos = 0;
            for (CreateQuestionRequest.OptionRequest opt : request.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(opt.getOptionText())
                        .isCorrect(opt.getIsCorrect() != null ? opt.getIsCorrect() : false)
                        .feedback(opt.getFeedback())
                        .position(pos++)
                        .build();
                question.getOptions().add(option);
            }
        }

        question = questionRepository.save(question);

        // Link media files
        if (request.getMediaIds() != null && !request.getMediaIds().isEmpty()) {
            int pos = 0;
            for (UUID mediaId : request.getMediaIds()) {
                MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_MDA_001));
                QuestionMedia qm = QuestionMedia.builder()
                        .question(question)
                        .mediaFile(mediaFile)
                        .position(pos++)
                        .build();
                questionMediaRepository.save(qm);
            }
        }

        log.info("Created question: {} [tenant={}]", question.getId(), tenant.getName());
        return toResponseFull(question);
    }

    @Transactional
    public QuestionResponse update(UUID tenantId, UUID id, UpdateQuestionRequest request) {
        Question question = questionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        if (request.getCategoryId() != null) {
            QuestionCategory category = categoryRepository.findByIdAndTenantId(request.getCategoryId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_005));
            question.setCategory(category);
        }
        if (request.getFolderId() != null) {
            QuestionFolder folder = folderRepository.findByIdAndTenantId(request.getFolderId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_004));
            question.setFolder(folder);
        }
        if (request.getQuestionText() != null) question.setQuestionText(request.getQuestionText());
        if (request.getDescription() != null) question.setDescription(request.getDescription());
        if (request.getExplanation() != null) question.setExplanation(request.getExplanation());
        if (request.getType() != null) question.setType(request.getType());
        if (request.getPoints() != null) question.setPoints(request.getPoints());
        if (request.getDifficultyLevel() != null) question.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getTimeEstimateSeconds() != null) question.setTimeEstimateSeconds(request.getTimeEstimateSeconds());
        if (request.getIsShared() != null) question.setIsShared(request.getIsShared());

        question = questionRepository.save(question);
        log.info("Updated question: {}", question.getId());
        return toResponseFull(question);
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Question question = questionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));
        questionRepository.delete(question);
        log.info("Deleted question: {}", id);
    }

    // ── Option management ───────────────────────────────────────────────────────

    @Transactional
    public OptionResponse addOption(UUID tenantId, UUID questionId, CreateOptionRequest request) {
        Question question = questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        QuestionOption option = QuestionOption.builder()
                .question(question)
                .optionText(request.getOptionText())
                .isCorrect(request.getIsCorrect() != null ? request.getIsCorrect() : false)
                .feedback(request.getFeedback())
                .position(request.getPosition() != null ? request.getPosition() : question.getOptions().size())
                .build();

        option = optionRepository.save(option);
        log.info("Added option {} to question {}", option.getId(), questionId);
        return toOptionResponse(option);
    }

    @Transactional
    public OptionResponse updateOption(UUID tenantId, UUID questionId, UUID optionId, UpdateOptionRequest request) {
        questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        QuestionOption option = optionRepository.findByIdAndQuestionId(optionId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_002));

        if (request.getOptionText() != null) option.setOptionText(request.getOptionText());
        if (request.getIsCorrect() != null) option.setIsCorrect(request.getIsCorrect());
        if (request.getFeedback() != null) option.setFeedback(request.getFeedback());
        if (request.getPosition() != null) option.setPosition(request.getPosition());

        option = optionRepository.save(option);
        log.info("Updated option {} on question {}", optionId, questionId);
        return toOptionResponse(option);
    }

    @Transactional
    public void deleteOption(UUID tenantId, UUID questionId, UUID optionId) {
        questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        QuestionOption option = optionRepository.findByIdAndQuestionId(optionId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_002));

        optionRepository.delete(option);
        log.info("Deleted option {} from question {}", optionId, questionId);
    }

    // ── Attachment management ───────────────────────────────────────────────────

    @Transactional
    public AttachmentResponse addAttachment(UUID tenantId, UUID questionId,
                                             String fileName, String filePath,
                                             String fileType, Integer fileSize) {
        Question question = questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        QuestionAttachment attachment = QuestionAttachment.builder()
                .question(question)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .fileSize(fileSize)
                .position(question.getAttachments().size())
                .build();

        attachment = attachmentRepository.save(attachment);
        log.info("Added attachment {} to question {}", attachment.getId(), questionId);
        return toAttachmentResponse(attachment);
    }

    @Transactional
    public void deleteAttachment(UUID tenantId, UUID questionId, UUID attachmentId) {
        questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        QuestionAttachment attachment = attachmentRepository.findByIdAndQuestionId(attachmentId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_003));

        attachmentRepository.delete(attachment);
        log.info("Deleted attachment {} from question {}", attachmentId, questionId);
    }

    // ── Media management (many-to-many via question_media) ──────────────────────

    @Transactional
    public List<MediaResponse> attachMedia(UUID tenantId, UUID questionId, List<UUID> mediaIds) {
        Question question = questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));

        List<QuestionMedia> existing = questionMediaRepository.findByQuestionIdOrderByPositionAsc(questionId);
        int startPos = existing.size();

        // Filter out already-attached media IDs
        var existingMediaIds = existing.stream()
                .map(qm -> qm.getMediaFile().getId())
                .collect(java.util.stream.Collectors.toSet());
        List<UUID> newMediaIds = mediaIds.stream()
                .filter(id -> !existingMediaIds.contains(id))
                .toList();

        if (!newMediaIds.isEmpty()) {
            // Batch fetch all media files
            List<MediaFile> mediaFiles = mediaFileRepository.findAllById(newMediaIds);
            if (mediaFiles.size() != newMediaIds.size()) {
                throw new ResourceNotFoundException(ErrorCode.SE_MDA_001);
            }

            List<QuestionMedia> toSave = new java.util.ArrayList<>();
            for (MediaFile mediaFile : mediaFiles) {
                toSave.add(QuestionMedia.builder()
                        .question(question)
                        .mediaFile(mediaFile)
                        .position(startPos++)
                        .build());
            }
            questionMediaRepository.saveAll(toSave);
        }
        return getMediaForQuestion(questionId);
    }

    @Transactional
    public void detachMedia(UUID tenantId, UUID questionId, UUID mediaId) {
        questionRepository.findByIdAndTenantId(questionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_001));
        questionMediaRepository.deleteByQuestionIdAndMediaFileId(questionId, mediaId);
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> getMediaForQuestion(UUID questionId) {
        return questionMediaRepository.findByQuestionIdOrderByPositionAsc(questionId)
                .stream().map(qm -> MediaResponse.from(qm.getMediaFile())).toList();
    }

    // ── Mapping helpers ─────────────────────────────────────────────────────────

    private QuestionResponse toResponseSummary(Question q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .tenantId(q.getTenant().getId())
                .categoryId(q.getCategory() != null ? q.getCategory().getId() : null)
                .categoryName(q.getCategory() != null ? q.getCategory().getName() : null)
                .folderId(q.getFolder() != null ? q.getFolder().getId() : null)
                .folderName(q.getFolder() != null ? q.getFolder().getName() : null)
                .questionText(q.getQuestionText())
                .type(q.getType())
                .points(q.getPoints())
                .position(q.getPosition())
                .difficultyLevel(q.getDifficultyLevel())
                .isShared(q.getIsShared())
                .createdById(q.getCreatedBy().getId())
                .createdByName(q.getCreatedBy().getName())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }

    private QuestionResponse toResponseFull(Question q) {
        QuestionResponse response = toResponseSummary(q);
        response.setDescription(q.getDescription());
        response.setExplanation(q.getExplanation());
        response.setTimeEstimateSeconds(q.getTimeEstimateSeconds());
        response.setPicture(q.getPicture());

        if (q.getOptions() != null) {
            response.setOptions(q.getOptions().stream().map(this::toOptionResponse).toList());
        }
        if (q.getAttachments() != null) {
            response.setAttachments(q.getAttachments().stream().map(this::toAttachmentResponse).toList());
        }

        List<QuestionMedia> questionMedia = questionMediaRepository.findByQuestionIdOrderByPositionAsc(q.getId());
        if (!questionMedia.isEmpty()) {
            response.setMedia(questionMedia.stream().map(qm -> MediaResponse.from(qm.getMediaFile())).toList());
        }

        return response;
    }

    private OptionResponse toOptionResponse(QuestionOption opt) {
        return OptionResponse.builder()
                .id(opt.getId())
                .optionText(opt.getOptionText())
                .isCorrect(opt.getIsCorrect())
                .weight(opt.getWeight())
                .position(opt.getPosition())
                .feedback(opt.getFeedback())
                .build();
    }

    private AttachmentResponse toAttachmentResponse(QuestionAttachment att) {
        return AttachmentResponse.builder()
                .id(att.getId())
                .fileName(att.getFileName())
                .filePath(att.getFilePath())
                .fileType(att.getFileType())
                .fileSize(att.getFileSize())
                .position(att.getPosition())
                .build();
    }
}
