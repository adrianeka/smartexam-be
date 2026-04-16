package com.tujuhsembilan.smartedutelu.domain.communication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequest {

    @NotNull
    private UUID receiverId;

    private String subject;

    @NotBlank
    private String content;
}
