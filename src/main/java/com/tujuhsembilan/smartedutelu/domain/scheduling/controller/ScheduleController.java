package com.tujuhsembilan.smartedutelu.domain.scheduling.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.CreateScheduleRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.UpdateScheduleRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.ScheduleResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Scheduling", description = "Jadwal ujian")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "Daftar jadwal (filter: examId, dateRange)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> listSchedules(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) UUID examId,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to) {
        List<ScheduleResponse> schedules = scheduleService.listSchedules(tenantId, examId, from, to);
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil diambil", schedules));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail jadwal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(@PathVariable UUID id) {
        ScheduleResponse schedule = scheduleService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Detail jadwal berhasil diambil", schedule));
    }

    @PostMapping
    @Operation(summary = "Buat jadwal ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request) {
        ScheduleResponse schedule = scheduleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Jadwal berhasil dibuat", schedule));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update jadwal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        ScheduleResponse schedule = scheduleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil diperbarui", schedule));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus jadwal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable UUID id) {
        scheduleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil dihapus", null));
    }
}
