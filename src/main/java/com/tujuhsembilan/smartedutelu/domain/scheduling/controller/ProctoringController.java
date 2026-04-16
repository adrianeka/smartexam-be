package com.tujuhsembilan.smartedutelu.domain.scheduling.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.LogCheatingRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.CheatingLogResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.ProctorAssignmentResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.SessionResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.service.ProctoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/proctoring")
@RequiredArgsConstructor
@Tag(name = "Proctoring", description = "Monitoring sesi ujian secara live")
public class ProctoringController {

    private final ProctoringService proctoringService;

    @GetMapping("/sessions")
    @Operation(summary = "List sesi aktif (monitoring)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> listActiveSessions(
            @RequestParam UUID tenantId) {
        List<SessionResponse> sessions = proctoringService.listActiveSessions(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Sesi aktif berhasil diambil", sessions));
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Detail sesi + cheating logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<SessionResponse>> getSessionDetail(@PathVariable UUID id) {
        SessionResponse session = proctoringService.getSessionDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Detail sesi berhasil diambil", session));
    }

    @PostMapping("/sessions/{id}/cheating-logs")
    @Operation(summary = "Log aktivitas mencurigakan")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CheatingLogResponse>> logCheating(
            @PathVariable UUID id,
            @Valid @RequestBody LogCheatingRequest request) {
        CheatingLogResponse cheatingLog = proctoringService.logCheating(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cheating log berhasil dicatat", cheatingLog));
    }

    @PostMapping("/sessions/{id}/terminate")
    @Operation(summary = "Akhiri sesi paksa")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<SessionResponse>> terminateSession(@PathVariable UUID id) {
        SessionResponse session = proctoringService.terminateSession(id);
        return ResponseEntity.ok(ApiResponse.success("Sesi berhasil diakhiri", session));
    }

    @GetMapping("/assignments")
    @Operation(summary = "List penugasan proctor saya")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'PROCTOR')")
    public ResponseEntity<ApiResponse<List<ProctorAssignmentResponse>>> getMyAssignments() {
        List<ProctorAssignmentResponse> assignments = proctoringService.getMyAssignments();
        return ResponseEntity.ok(ApiResponse.success("Penugasan proctor berhasil diambil", assignments));
    }
}
