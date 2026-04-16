package com.tujuhsembilan.smartedutelu.domain.question.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.CreateFolderRequest;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.UpdateFolderRequest;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.FolderResponse;
import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionFolder;
import com.tujuhsembilan.smartedutelu.domain.question.repository.QuestionFolderRepository;
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
public class QuestionFolderService {

    private final QuestionFolderRepository folderRepository;
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public List<FolderResponse> getTree(UUID tenantId) {
        List<QuestionFolder> all = folderRepository.findByTenantIdOrderByPositionAscNameAsc(tenantId);
        return buildTree(all, null);
    }

    @Transactional(readOnly = true)
    public FolderResponse getById(UUID tenantId, UUID id) {
        QuestionFolder folder = findOrThrow(tenantId, id);
        FolderResponse response = toResponseFlat(folder);
        List<FolderResponse> children = folder.getChildren().stream()
                .map(this::toResponseFlat)
                .toList();
        if (!children.isEmpty()) {
            response.setChildren(children);
        }
        return response;
    }

    @Transactional
    public FolderResponse create(CreateFolderRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        QuestionFolder parent = null;
        if (request.getParentId() != null) {
            parent = findOrThrow(request.getTenantId(), request.getParentId());
        }

        QuestionFolder folder = folderRepository.save(QuestionFolder.builder()
                .tenant(tenant)
                .name(request.getName())
                .description(request.getDescription())
                .parent(parent)
                .position(request.getPosition() != null ? request.getPosition() : 0)
                .build());

        log.info("Created question folder: {} [tenant={}]", folder.getName(), tenant.getName());
        return toResponseFlat(folder);
    }

    @Transactional
    public FolderResponse update(UUID tenantId, UUID id, UpdateFolderRequest request) {
        QuestionFolder folder = findOrThrow(tenantId, id);

        if (request.getName() != null) {
            folder.setName(request.getName());
        }
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription());
        }
        if (request.getPosition() != null) {
            folder.setPosition(request.getPosition());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.SE_CAT_003);
            }
            QuestionFolder newParent = findOrThrow(tenantId, request.getParentId());
            if (isDescendant(id, newParent)) {
                throw new BusinessException(ErrorCode.SE_CAT_004);
            }
            folder.setParent(newParent);
        }

        log.info("Updated question folder: {}", folder.getName());
        return toResponseFlat(folderRepository.save(folder));
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        QuestionFolder folder = findOrThrow(tenantId, id);
        folderRepository.delete(folder);
        log.info("Deleted question folder: {}", folder.getName());
    }

    public QuestionFolder findOrThrow(UUID tenantId, UUID id) {
        return folderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_QST_004));
    }

    private List<FolderResponse> buildTree(List<QuestionFolder> all, UUID parentId) {
        return all.stream()
                .filter(f -> parentId == null
                        ? f.getParent() == null
                        : f.getParent() != null && f.getParent().getId().equals(parentId))
                .map(f -> {
                    FolderResponse r = toResponseFlat(f);
                    List<FolderResponse> children = buildTree(all, f.getId());
                    if (!children.isEmpty()) {
                        r.setChildren(children);
                    }
                    return r;
                })
                .toList();
    }

    private boolean isDescendant(UUID ancestorId, QuestionFolder potentialDescendant) {
        QuestionFolder current = potentialDescendant;
        while (current.getParent() != null) {
            if (current.getParent().getId().equals(ancestorId)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private FolderResponse toResponseFlat(QuestionFolder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .tenantId(folder.getTenant().getId())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .name(folder.getName())
                .description(folder.getDescription())
                .position(folder.getPosition())
                .build();
    }
}
