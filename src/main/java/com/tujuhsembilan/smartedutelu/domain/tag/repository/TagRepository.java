package com.tujuhsembilan.smartedutelu.domain.tag.repository;

import com.tujuhsembilan.smartedutelu.domain.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    Page<Tag> findByType(String type, Pageable pageable);

    Optional<Tag> findBySlugAndType(String slug, String type);

    boolean existsBySlugAndType(String slug, String type);
}
