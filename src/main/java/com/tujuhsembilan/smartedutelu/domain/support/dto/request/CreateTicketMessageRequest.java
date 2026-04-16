package com.tujuhsembilan.smartedutelu.domain.support.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTicketMessageRequest {

    @NotBlank
    private String content;

    private String attachmentUrl;
}
