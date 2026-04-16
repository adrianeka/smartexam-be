package com.tujuhsembilan.smartedutelu.domain.exam.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.CreateExamCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.UpdateExamCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamCategoryResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamCategory;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamCategoryRepository;
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
public class ExamCategoryService {

    private final ExamCategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;

    // ── Read ────────────────────────────────────────────────────────────────────

    /**
     * Returns a full tree for a tenant: root categories with children nested recursively.
     */
    @Transactional(readOnly = true)
    public List<ExamCategoryResponse> getTree(UUID tenantId) {
        // Load all categories flat, then build tree in-memory (avoids recursive SQL)
        List<ExamCategory> all = categoryRepository.findByTenantIdOrderByPositionAscNameAsc(tenantId);
        return buildTree(all, null);
    }

    /**
     * Returns a single category with its direct children.
     */
    @Transactional(readOnly = true)
    public ExamCategoryResponse getById(UUID tenantId, UUID id) {
        ExamCategory cat = findOrThrow(tenantId, id);
        List<ExamCategory> children = categoryRepository.findByParentIdOrderByPositionAscNameAsc(id);
        ExamCategoryResponse response = toResponse(cat);
        response.setChildren(children.stream().map(this::toResponseFlat).toList());
        return response;
    }

    // ── Write ───────────────────────────────────────────────────────────────────

    @Transactional
    public ExamCategoryResponse create(CreateExamCategoryRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        if (categoryRepository.existsBySlugAndTenantId(request.getSlug(), request.getTenantId())) {
            throw new DuplicateResourceException(ErrorCode.SE_CAT_002);
        }

        ExamCategory parent = null;
        if (request.getParentId() != null) {
            parent = findOrThrow(request.getTenantId(), request.getParentId());
        }

        ExamCategory cat = categoryRepository.save(ExamCategory.builder()
                .tenant(tenant)
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .parent(parent)
                .position(request.getPosition() != null ? request.getPosition() : 0)
                .build());

        log.info("Created exam category: {} [tenant={}]", cat.getName(), tenant.getName());
        return toResponseFlat(cat);
    }

    @Transactional
    public ExamCategoryResponse update(UUID tenantId, UUID id, UpdateExamCategoryRequest request) {
        ExamCategory cat = findOrThrow(tenantId, id);

        if (request.getSlug() != null) {
            if (categoryRepository.existsBySlugAndTenantIdAndIdNot(request.getSlug(), tenantId, id)) {
                throw new DuplicateResourceException(ErrorCode.SE_CAT_002);
            }
            cat.setSlug(request.getSlug());
        }
        if (request.getName() != null) {
            cat.setName(request.getName());
        }
        if (request.getDescription() != null) {
            cat.setDescription(request.getDescription());
        }
        if (request.getPosition() != null) {
            cat.setPosition(request.getPosition());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.SE_CAT_003);
            }
            ExamCategory newParent = findOrThrow(tenantId, request.getParentId());
            // Prevent circular reference: new parent must not be a descendant of this node
            if (isDescendant(id, newParent, tenantId)) {
                throw new BusinessException(ErrorCode.SE_CAT_004);
            }
            cat.setParent(newParent);
        }

        log.info("Updated exam category: {}", cat.getName());
        return toResponseFlat(categoryRepository.save(cat));
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        ExamCategory cat = findOrThrow(tenantId, id);
        categoryRepository.delete(cat);
        log.info("Deleted exam category: {}", cat.getName());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    public ExamCategory findOrThrow(UUID tenantId, UUID id) {
        return categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAT_001));
    }

    /** Builds a nested tree from a flat list, grouping by parentId. */
    private List<ExamCategoryResponse> buildTree(List<ExamCategory> all, UUID parentId) {
        return all.stream()
                .filter(c -> parentId == null
                        ? c.getParent() == null
                        : c.getParent() != null && c.getParent().getId().equals(parentId))
                .map(c -> {
                    ExamCategoryResponse r = toResponseFlat(c);
                    List<ExamCategoryResponse> children = buildTree(all, c.getId());
                    if (!children.isEmpty()) {
                        r.setChildren(children);
                    }
                    return r;
                })
                .toList();
    }

    /** Checks whether `potentialDescendant` is a descendant of `ancestorId`. */
    private boolean isDescendant(UUID ancestorId, ExamCategory potentialDescendant, UUID tenantId) {
        ExamCategory current = potentialDescendant;
        while (current.getParent() != null) {
            if (current.getParent().getId().equals(ancestorId)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private ExamCategoryResponse toResponseFlat(ExamCategory cat) {
        return ExamCategoryResponse.builder()
                .id(cat.getId())
                .tenantId(cat.getTenant().getId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .description(cat.getDescription())
                .parentId(cat.getParent() != null ? cat.getParent().getId() : null)
                .position(cat.getPosition())
                .build();
    }

    private ExamCategoryResponse toResponse(ExamCategory cat) {
        return toResponseFlat(cat);
    }
}
