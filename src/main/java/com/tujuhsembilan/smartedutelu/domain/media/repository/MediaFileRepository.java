package com.tujuhsembilan.smartedutelu.domain.media.repository;

import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

    Page<MediaFile> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<MediaFile> findByOwnerIdOrderByUploadedAtDesc(UUID ownerId, Pageable pageable);

    List<MediaFile> findByContextAndContextId(String context, UUID contextId);
}
