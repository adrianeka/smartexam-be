package com.tujuhsembilan.smartedutelu.domain.question.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentResponse {

    private UUID id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Integer fileSize;
    private Integer position;
}
