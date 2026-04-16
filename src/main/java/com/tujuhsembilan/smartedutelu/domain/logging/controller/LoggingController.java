package com.tujuhsembilan.smartedutelu.domain.logging.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.ActivityLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.AuditLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.EventResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.LoginLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/logs")
@RequiredArgsConstructor
@Tag(name = "Logging", description = "Event, activity, audit & login logs")
public class LoggingController {

    private final LoggingService loggingService;

    @GetMapping("/events")
    @Operation(summary = "Daftar domain events")
    @PreAuthorize("hasAuthority('VIEW_LOGS')")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> listEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(loggingService.listEvents(eventType, userId, pageable))));
    }

    @GetMapping("/activities")
    @Operation(summary = "Daftar activity logs")
    @PreAuthorize("hasAuthority('VIEW_LOGS')")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> listActivities(
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(loggingService.listActivityLogs(userId, pageable))));
    }

    @GetMapping("/audits")
    @Operation(summary = "Daftar audit logs")
    @PreAuthorize("hasAuthority('VIEW_LOGS')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> listAudits(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(loggingService.listAuditLogs(entityType, entityId, pageable))));
    }

    @GetMapping("/logins")
    @Operation(summary = "Daftar login logs")
    @PreAuthorize("hasAuthority('VIEW_LOGS')")
    public ResponseEntity<ApiResponse<PageResponse<LoginLogResponse>>> listLogins(
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(loggingService.listLoginLogs(userId, pageable))));
    }
}
