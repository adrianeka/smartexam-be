package com.tujuhsembilan.smartedutelu.domain.media.dto.response;

import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MediaResponse {

    private UUID id;
    private UUID ownerId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String context;
    private UUID contextId;
    private OffsetDateTime uploadedAt;

    public static MediaResponse from(MediaFile m) {
        return MediaResponse.builder()
                .id(m.getId())
                .ownerId(m.getOwner().getId())
                .fileName(m.getFileName())
                .filePath(m.getFilePath())
                .fileType(m.getFileType())
                .fileSize(m.getFileSize())
                .context(m.getContext())
                .contextId(m.getContextId())
                .uploadedAt(m.getUploadedAt())
                .build();
    }
}
