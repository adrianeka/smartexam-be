package com.tujuhsembilan.smartedutelu.domain.tag.repository;

import com.tujuhsembilan.smartedutelu.domain.tag.entity.Taggable;
import com.tujuhsembilan.smartedutelu.domain.tag.enums.TaggableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaggableRepository extends JpaRepository<Taggable, UUID> {

    List<Taggable> findByTaggableTypeAndTaggableId(TaggableType taggableType, UUID taggableId);

    List<Taggable> findByTagId(UUID tagId);

    Optional<Taggable> findByTagIdAndTaggableTypeAndTaggableId(UUID tagId, TaggableType taggableType, UUID taggableId);

    void deleteByTaggableTypeAndTaggableId(TaggableType taggableType, UUID taggableId);
}
