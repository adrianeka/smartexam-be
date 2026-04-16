package com.tujuhsembilan.smartedutelu.domain.question.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolderResponse {

    private UUID id;
    private UUID tenantId;
    private UUID parentId;
    private String name;
    private String description;
    private Integer position;
    private List<FolderResponse> children;
}
