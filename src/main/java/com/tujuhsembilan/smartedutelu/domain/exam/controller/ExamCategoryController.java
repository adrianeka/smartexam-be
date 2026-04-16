package com.tujuhsembilan.smartedutelu.domain.exam.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.CreateExamCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.UpdateExamCategoryRequest;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamCategoryResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.service.ExamCategoryService;
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
@RequestMapping("/v1/exam-categories")
@RequiredArgsConstructor
@Tag(name = "Exam Categories", description = "Kategori ujian hierarki (tree)")
public class ExamCategoryController {

    private final ExamCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Daftar kategori sebagai tree per tenant")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamCategoryResponse>>> getTree(
            @RequestParam UUID tenantId) {
        List<ExamCategoryResponse> tree = categoryService.getTree(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil diambil", tree));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail kategori + direct children")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamCategoryResponse>> getById(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        ExamCategoryResponse cat = categoryService.getById(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil diambil", cat));
    }

    @PostMapping
    @Operation(summary = "Buat kategori baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamCategoryResponse>> create(
            @Valid @RequestBody CreateExamCategoryRequest request) {
        ExamCategoryResponse cat = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kategori berhasil dibuat", cat));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update kategori")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamCategoryResponse>> update(
            @RequestParam UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExamCategoryRequest request) {
        ExamCategoryResponse cat = categoryService.update(tenantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil diperbarui", cat));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus kategori")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        categoryService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil dihapus", null));
    }
}
