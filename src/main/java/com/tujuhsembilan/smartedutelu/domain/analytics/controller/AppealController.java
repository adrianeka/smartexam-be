package com.tujuhsembilan.smartedutelu.domain.analytics.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.request.CreateAppealRequest;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.request.ResolveAppealRequest;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.AppealResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.service.AppealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/appeals")
@RequiredArgsConstructor
@Tag(name = "Appeals", description = "Banding hasil ujian")
public class AppealController {

    private final AppealService appealService;

    @GetMapping
    @Operation(summary = "Daftar banding (filter: status)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<AppealResponse>>> list(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(appealService.listAppeals(status, pageable))));
    }

    @PostMapping
    @Operation(summary = "Ajukan banding")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AppealResponse>> create(@Valid @RequestBody CreateAppealRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(appealService.createAppeal(request)));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Selesaikan banding (approve/reject)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AppealResponse>> resolve(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveAppealRequest request) {
        return ResponseEntity.ok(ApiResponse.success(appealService.resolveAppeal(id, request)));
    }
}
