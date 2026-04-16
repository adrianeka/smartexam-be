package com.tujuhsembilan.smartedutelu.domain.analytics.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.ExamResultResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.service.ResultService;
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
@RequestMapping("/v1/results")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Hasil ujian resmi")
public class ResultController {

    private final ResultService resultService;

    @GetMapping
    @Operation(summary = "Daftar hasil ujian (filter: examId, userId)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<ExamResultResponse>>> list(
            @RequestParam(required = false) UUID examId,
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(resultService.listResults(examId, userId, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail hasil ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<ExamResultResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(resultService.getResult(id)));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "Publikasi hasil ke siswa")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamResultResponse>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(resultService.publishResult(id)));
    }
}
