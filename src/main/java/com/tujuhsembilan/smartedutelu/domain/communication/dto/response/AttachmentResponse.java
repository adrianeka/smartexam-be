package com.tujuhsembilan.smartedutelu.domain.communication.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AttachmentResponse {

    private UUID id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Integer fileSize;
}
