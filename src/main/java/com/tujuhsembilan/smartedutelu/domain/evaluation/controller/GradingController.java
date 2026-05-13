package com.tujuhsembilan.smartedutelu.domain.evaluation.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.GradeAnswerRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.AttemptAnswerResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.AttemptResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.service.GradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/grading")
@RequiredArgsConstructor
@Tag(name = "Grading", description = "Penilaian & hasil ujian")
public class GradingController {

    private final GradingService gradingService;

    @GetMapping("/pending")
    @Operation(summary = "Daftar attempt yang belum dinilai")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<AttemptResponse>>> listPending(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(gradingService.listPendingGrading(pageable))));
    }

    @GetMapping("/attempts/{attemptId}")
    @Operation(summary = "Detail attempt beserta jawaban")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AttemptResponse>> getAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.getAttemptDetail(attemptId)));
    }

    @PutMapping("/attempts/{attemptId}/answers/{answerId}")
    @Operation(summary = "Nilai jawaban siswa")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttemptAnswerResponse>> gradeAnswer(
            @PathVariable UUID attemptId,
            @PathVariable UUID answerId,
            @Valid @RequestBody GradeAnswerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.gradeAnswer(attemptId, answerId, request)));
    }

    @PostMapping("/attempts/{attemptId}/finalize")
    @Operation(summary = "Finalisasi penilaian attempt (hitung total skor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AttemptResponse>> finalizeAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.finalizeAttempt(attemptId)));
    }

    @GetMapping("/results")
    @Operation(summary = "Daftar hasil ujian (filter: examId)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<AttemptResponse>>> listResults(
            @RequestParam(required = false) UUID examId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(gradingService.listResults(examId, pageable))));
    }

    @PutMapping("/results/{attemptId}/publish")
    @Operation(summary = "Publikasi hasil ujian ke siswa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttemptResponse>> publishResult(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.publishResult(attemptId)));
    }
}
