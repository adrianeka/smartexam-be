package com.tujuhsembilan.smartedutelu.domain.tenant.dto.response;

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
public class MemberResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String role;
}
