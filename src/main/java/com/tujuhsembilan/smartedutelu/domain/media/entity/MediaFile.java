package com.tujuhsembilan.smartedutelu.domain.media.entity;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "media_files")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(length = 100)
    private String context;

    @Column(name = "context_id")
    private UUID contextId;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime uploadedAt = OffsetDateTime.now();
}
