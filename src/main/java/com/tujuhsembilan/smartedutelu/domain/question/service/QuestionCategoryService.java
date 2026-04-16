package com.tujuhsembilan.smartedutelu.domain.question.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.CreateQuestionCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.UpdateQuestionCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.QuestionCategoryResponse;
import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionCategory;
import com.tujuhsembilan.smartedutelu.domain.question.repository.QuestionCategoryRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCategoryService {

    private final QuestionCategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public List<QuestionCategoryResponse> listByTenant(UUID tenantId) {
        return categoryRepository.findByTenantIdOrderByNameAsc(tenantId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionCategoryResponse getById(UUID tenantId, UUID id) {
        return toResponse(findOrThrow(tenantId, id));
    }

    @Transactional
    public QuestionCategoryResponse create(CreateQuestionCategoryRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        if (categoryRepository.existsByNameAndTenantId(request.getName(), request.getTenantId())) {
            throw new DuplicateResourceException(ErrorCode.SE_QST_006);
        }

        QuestionCategory category = categoryRepository.save(QuestionCategory.builder()
                .tenant(tenant)
                .name(request.getName())
                .description(request.getDescription())
                .build());

        log.info("Created question category: {} [tenant={}]", category.getName(), tenant.getName());
        return toResponse(category);
    }

    @Transactional
    public QuestionCategoryResponse update(UUID tenantId, UUID id, UpdateQuestionCategoryRequest request) {
        QuestionCategory category = findOrThrow(tenantId, id);

        if (request.getName() != null) {
            if (categoryRepository.existsByNameAndTenantIdAndIdNot(request.getName(), tenantId, id)) {
                throw new DuplicateResourceException(ErrorCode.SE_QST_006);
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        log.info("Updated question category: {}", category.getName());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        QuestionCategory category = findOrThrow(tenantId, id);
        categoryRepository.delete(category);
        log.info("Deleted question category: {}", category.getName());
    }

    public QuestionCategory findOrThrow(UUID tenantId, UUID id) {
        return categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_005));
    }

    private QuestionCategoryResponse toResponse(QuestionCategory category) {
        return QuestionCategoryResponse.builder()
                .id(category.getId())
                .tenantId(category.getTenant().getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
