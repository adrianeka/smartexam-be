package com.tujuhsembilan.smartedutelu.domain.tenant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantResponse {

    private UUID id;
    private String name;
    private String domain;
    private String logo;
    private String status;
    private OffsetDateTime createdAt;
}
