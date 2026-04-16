package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 50)
    private String phone;

    private String picture;

    @Size(max = 10)
    private String locale;

    @Size(max = 100)
    private String timezone;
}
