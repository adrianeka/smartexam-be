package com.tujuhsembilan.smartedutelu.domain.analytics.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.AnalyticsResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Statistik & analitik ujian")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/exams/{examId}")
    @Operation(summary = "Lihat analytics per ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getByExam(@PathVariable UUID examId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getAnalytics(examId)));
    }

    @PostMapping("/exams/{examId}/recalculate")
    @Operation(summary = "Hitung ulang analytics ujian")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> recalculate(@PathVariable UUID examId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.recalculate(examId)));
    }
}
