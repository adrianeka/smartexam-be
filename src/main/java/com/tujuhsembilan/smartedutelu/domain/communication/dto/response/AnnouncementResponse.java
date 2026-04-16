package com.tujuhsembilan.smartedutelu.domain.communication.dto.response;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.Announcement;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AnnouncementResponse {

    private UUID id;
    private UUID tenantId;
    private String title;
    private String message;
    private UUID createdBy;
    private Boolean emailSent;
    private OffsetDateTime createdAt;
    private List<AttachmentResponse> attachments;

    public static AnnouncementResponse from(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .tenantId(a.getTenant().getId())
                .title(a.getTitle())
                .message(a.getMessage())
                .createdBy(a.getCreatedBy().getId())
                .emailSent(a.getEmailSent())
                .createdAt(a.getCreatedAt())
                .attachments(a.getAttachments() != null
                        ? a.getAttachments().stream().map(att -> AttachmentResponse.builder()
                            .id(att.getId()).fileName(att.getFileName()).filePath(att.getFilePath())
                            .fileType(att.getFileType()).fileSize(att.getFileSize()).build()).toList()
                        : List.of())
                .build();
    }
}
