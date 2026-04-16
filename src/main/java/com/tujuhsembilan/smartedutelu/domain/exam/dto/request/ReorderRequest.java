package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReorderRequest {

    @NotEmpty(message = "Daftar urutan tidak boleh kosong")
    private List<UUID> orderedIds;
}
