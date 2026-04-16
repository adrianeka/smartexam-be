package com.tujuhsembilan.smartedutelu.domain.tag.repository;

import com.tujuhsembilan.smartedutelu.domain.tag.entity.Taggable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaggableRepository extends JpaRepository<Taggable, UUID> {

    List<Taggable> findByTaggableTypeAndTaggableId(String taggableType, UUID taggableId);

    List<Taggable> findByTagId(UUID tagId);

    Optional<Taggable> findByTagIdAndTaggableTypeAndTaggableId(UUID tagId, String taggableType, UUID taggableId);

    void deleteByTaggableTypeAndTaggableId(String taggableType, UUID taggableId);
}
