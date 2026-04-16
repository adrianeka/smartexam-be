package com.tujuhsembilan.smartedutelu.domain.support.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTicketRequest {

    private UUID examId;
    private UUID categoryId;
    private UUID priorityId;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
