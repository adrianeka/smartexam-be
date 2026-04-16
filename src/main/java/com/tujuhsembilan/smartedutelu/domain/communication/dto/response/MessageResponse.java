package com.tujuhsembilan.smartedutelu.domain.communication.dto.response;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MessageResponse {

    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String subject;
    private String content;
    private Boolean isRead;
    private OffsetDateTime sentAt;
    private List<AttachmentResponse> attachments;

    public static MessageResponse from(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSender().getId())
                .receiverId(m.getReceiver().getId())
                .subject(m.getSubject())
                .content(m.getContent())
                .isRead(m.getIsRead())
                .sentAt(m.getSentAt())
                .attachments(m.getAttachments() != null
                        ? m.getAttachments().stream().map(att -> AttachmentResponse.builder()
                            .id(att.getId()).fileName(att.getFileName()).filePath(att.getFilePath())
                            .fileType(att.getFileType()).fileSize(att.getFileSize()).build()).toList()
                        : List.of())
                .build();
    }
}
