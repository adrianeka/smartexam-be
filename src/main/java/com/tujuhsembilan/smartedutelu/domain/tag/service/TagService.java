package com.tujuhsembilan.smartedutelu.domain.tag.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.request.AttachTagRequest;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.request.CreateTagRequest;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.response.TagResponse;
import com.tujuhsembilan.smartedutelu.domain.tag.entity.Tag;
import com.tujuhsembilan.smartedutelu.domain.tag.entity.Taggable;
import com.tujuhsembilan.smartedutelu.domain.tag.repository.TagRepository;
import com.tujuhsembilan.smartedutelu.domain.tag.repository.TaggableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final TaggableRepository taggableRepository;

    @Transactional(readOnly = true)
    public Page<TagResponse> listByType(String type, Pageable pageable) {
        if (type != null) {
            return tagRepository.findByType(type, pageable).map(TagResponse::from);
        }
        return tagRepository.findAll(pageable).map(TagResponse::from);
    }

    @Transactional
    public TagResponse createTag(CreateTagRequest request) {
        if (tagRepository.existsBySlugAndType(request.getSlug(), request.getType())) {
            throw new DuplicateResourceException(ErrorCode.SE_TAG_002);
        }
        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .type(request.getType())
                .build();
        return TagResponse.from(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TAG_001));
        tagRepository.delete(tag);
    }

    @Transactional
    public void attachTag(AttachTagRequest request) {
        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TAG_001));

        taggableRepository.findByTagIdAndTaggableTypeAndTaggableId(
                request.getTagId(), request.getTaggableType(), request.getTaggableId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(ErrorCode.SE_TAG_002);
                });

        Taggable taggable = Taggable.builder()
                .tag(tag)
                .taggableType(request.getTaggableType())
                .taggableId(request.getTaggableId())
                .build();
        taggableRepository.save(taggable);
    }

    @Transactional
    public void detachTag(UUID tagId, String taggableType, UUID taggableId) {
        Taggable taggable = taggableRepository.findByTagIdAndTaggableTypeAndTaggableId(tagId, taggableType, taggableId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TAG_001));
        taggableRepository.delete(taggable);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTagsForEntity(String taggableType, UUID taggableId) {
        return taggableRepository.findByTaggableTypeAndTaggableId(taggableType, taggableId)
                .stream().map(t -> TagResponse.from(t.getTag())).toList();
    }
}
