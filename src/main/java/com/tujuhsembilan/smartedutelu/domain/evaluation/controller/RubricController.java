package com.tujuhsembilan.smartedutelu.domain.evaluation.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.CreateCriteriaRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.CreateRubricRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.UpdateCriteriaRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request.UpdateRubricRequest;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.CriteriaResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response.RubricResponse;
import com.tujuhsembilan.smartedutelu.domain.evaluation.service.RubricService;
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
@RequestMapping("/v1/rubrics")
@RequiredArgsConstructor
@Tag(name = "Rubrics", description = "Rubrik penilaian")
public class RubricController {

    private final RubricService rubricService;

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Daftar rubrik berdasarkan soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<RubricResponse>>> getByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(ApiResponse.success(rubricService.getRubricsByQuestion(questionId)));
    }

    @PostMapping
    @Operation(summary = "Buat rubrik baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<RubricResponse>> create(@Valid @RequestBody CreateRubricRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rubricService.createRubric(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rubrik")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<RubricResponse>> update(@PathVariable UUID id, @Valid @RequestBody UpdateRubricRequest request) {
        return ResponseEntity.ok(ApiResponse.success(rubricService.updateRubric(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus rubrik")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        rubricService.deleteRubric(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/criteria")
    @Operation(summary = "Tambah kriteria ke rubrik")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CriteriaResponse>> addCriteria(@PathVariable UUID id, @Valid @RequestBody CreateCriteriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rubricService.addCriteria(id, request)));
    }

    @PutMapping("/{id}/criteria/{criteriaId}")
    @Operation(summary = "Update kriteria rubrik")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CriteriaResponse>> updateCriteria(@PathVariable UUID id, @PathVariable UUID criteriaId,
                                                                         @Valid @RequestBody UpdateCriteriaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(rubricService.updateCriteria(id, criteriaId, request)));
    }

    @DeleteMapping("/{id}/criteria/{criteriaId}")
    @Operation(summary = "Hapus kriteria dari rubrik")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteCriteria(@PathVariable UUID id, @PathVariable UUID criteriaId) {
        rubricService.deleteCriteria(id, criteriaId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
