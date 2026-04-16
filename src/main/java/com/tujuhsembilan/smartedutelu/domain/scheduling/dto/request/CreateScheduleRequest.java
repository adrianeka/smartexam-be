package com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateScheduleRequest {

    @NotNull(message = "Exam ID tidak boleh kosong")
    private UUID examId;

    @NotNull(message = "Waktu mulai tidak boleh kosong")
    private OffsetDateTime startTime;

    @NotNull(message = "Waktu selesai tidak boleh kosong")
    private OffsetDateTime endTime;

    private Integer maxParticipants;

    private String location;
}
