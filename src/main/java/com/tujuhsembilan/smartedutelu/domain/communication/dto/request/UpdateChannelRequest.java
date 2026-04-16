package com.tujuhsembilan.smartedutelu.domain.communication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateChannelRequest {

    @NotBlank
    private String channel;

    private Boolean isEnabled;

    private Map<String, Object> preferences;
}
