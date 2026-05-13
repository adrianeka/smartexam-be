package com.tujuhsembilan.smartedutelu.domain.exam.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamQuestionResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.ExamResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.dto.response.SectionResponse;
import com.tujuhsembilan.smartedutelu.domain.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/exams")
@RequiredArgsConstructor
@Tag(name = "Exam Management", description = "Manajemen ujian, section, dan soal ujian")
public class ExamController {

    private final ExamService examService;

    // ══════════════════════════════════════════════════════════════════════════════
    // EXAM CRUD (7 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Daftar ujian dengan filter & pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> listExams(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<ExamResponse> result = examService.listExams(tenantId, status, examType, categoryId, keyword,
                pageable);
        return ResponseEntity.ok(ApiResponse.success("Daftar ujian berhasil diambil", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail ujian lengkap (+ sections + questions)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        ExamResponse exam = examService.getById(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Detail ujian berhasil diambil", exam));
    }

    @PostMapping
    @Operation(summary = "Buat ujian baru (status: draft)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @Valid @RequestBody CreateExamRequest request) {
        ExamResponse exam = examService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ujian berhasil dibuat", exam));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ujian (hanya draft)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @RequestParam UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExamRequest request) {
        ExamResponse exam = examService.update(tenantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Ujian berhasil diperbarui", exam));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteExam(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        examService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Ujian berhasil dihapus", null));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "Publish ujian (draft → published)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> publishExam(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        ExamResponse exam = examService.publish(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Ujian berhasil di-publish", exam));
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> archiveExam(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        ExamResponse exam = examService.archive(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Ujian berhasil diarsipkan", exam));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // SECTIONS (5 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping("/{examId}/sections")
    @Operation(summary = "Daftar section dalam ujian")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> listSections(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId) {
        List<SectionResponse> sections = examService.listSections(tenantId, examId);
        return ResponseEntity.ok(ApiResponse.success("Section berhasil diambil", sections));
    }

    @PostMapping("/{examId}/sections")
    @Operation(summary = "Buat section baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @Valid @RequestBody CreateSectionRequest request) {
        SectionResponse section = examService.createSection(tenantId, examId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Section berhasil dibuat", section));
    }

    @PutMapping("/{examId}/sections/{sectionId}")
    @Operation(summary = "Update section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateSectionRequest request) {
        SectionResponse section = examService.updateSection(tenantId, examId, sectionId, request);
        return ResponseEntity.ok(ApiResponse.success("Section berhasil diperbarui", section));
    }

    @DeleteMapping("/{examId}/sections/{sectionId}")
    @Operation(summary = "Hapus section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId) {
        examService.deleteSection(tenantId, examId, sectionId);
        return ResponseEntity.ok(ApiResponse.success("Section berhasil dihapus", null));
    }

    @PutMapping("/{examId}/sections/reorder")
    @Operation(summary = "Reorder sections")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> reorderSections(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @Valid @RequestBody ReorderRequest request) {
        List<SectionResponse> sections = examService.reorderSections(tenantId, examId, request);
        return ResponseEntity.ok(ApiResponse.success("Urutan section berhasil diperbarui", sections));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // EXAM QUESTIONS inside sections (5 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping("/{examId}/sections/{sectionId}/questions")
    @Operation(summary = "Daftar soal di section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamQuestionResponse>>> listSectionQuestions(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId) {
        List<ExamQuestionResponse> questions = examService.listSectionQuestions(tenantId, examId, sectionId);
        return ResponseEntity.ok(ApiResponse.success("Soal section berhasil diambil", questions));
    }

    @PostMapping("/{examId}/sections/{sectionId}/questions")
    @Operation(summary = "Tambahkan soal ke section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamQuestionResponse>>> addQuestionsToSection(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody AddQuestionsToSectionRequest request) {
        List<ExamQuestionResponse> questions = examService.addQuestionsToSection(tenantId, examId, sectionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Soal berhasil ditambahkan ke section", questions));
    }

    @PutMapping("/{examId}/sections/{sectionId}/questions/{eqId}")
    @Operation(summary = "Update weight/position soal di section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamQuestionResponse>> updateExamQuestion(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId,
            @PathVariable UUID eqId,
            @Valid @RequestBody UpdateExamQuestionRequest request) {
        ExamQuestionResponse eq = examService.updateExamQuestion(tenantId, examId, sectionId, eqId, request);
        return ResponseEntity.ok(ApiResponse.success("Soal section berhasil diperbarui", eq));
    }

    @DeleteMapping("/{examId}/sections/{sectionId}/questions/{eqId}")
    @Operation(summary = "Hapus soal dari section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteExamQuestion(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId,
            @PathVariable UUID eqId) {
        examService.deleteExamQuestion(tenantId, examId, sectionId, eqId);
        return ResponseEntity.ok(ApiResponse.success("Soal berhasil dihapus dari section", null));
    }

    @PutMapping("/{examId}/sections/{sectionId}/questions/reorder")
    @Operation(summary = "Reorder soal di section")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamQuestionResponse>>> reorderSectionQuestions(
            @RequestParam UUID tenantId,
            @PathVariable UUID examId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody ReorderRequest request) {
        List<ExamQuestionResponse> questions = examService.reorderSectionQuestions(tenantId, examId, sectionId,
                request);
        return ResponseEntity.ok(ApiResponse.success("Urutan soal berhasil diperbarui", questions));
    }
}
