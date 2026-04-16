package com.tujuhsembilan.smartedutelu.domain.question.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.media.dto.response.MediaResponse;
import com.tujuhsembilan.smartedutelu.domain.question.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.question.dto.response.*;
import com.tujuhsembilan.smartedutelu.domain.question.service.QuestionCategoryService;
import com.tujuhsembilan.smartedutelu.domain.question.service.QuestionFolderService;
import com.tujuhsembilan.smartedutelu.domain.question.service.QuestionService;
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
@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Question Bank", description = "Bank soal, folder, dan kategori soal")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionFolderService folderService;
    private final QuestionCategoryService categoryService;

    // ══════════════════════════════════════════════════════════════════════════════
    //  QUESTIONS (5 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Daftar soal dengan filter & pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> listQuestions(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID folderId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<QuestionResponse> result = questionService.listQuestions(
                tenantId, type, difficulty, categoryId, folderId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Daftar soal berhasil diambil", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail soal lengkap (opsi + lampiran)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        QuestionResponse question = questionService.getById(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Detail soal berhasil diambil", question));
    }

    @PostMapping
    @Operation(summary = "Buat soal baru (dengan opsi inline)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse question = questionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Soal berhasil dibuat", question));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @RequestParam UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQuestionRequest request) {
        QuestionResponse question = questionService.update(tenantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Soal berhasil diperbarui", question));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        questionService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Soal berhasil dihapus", null));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  OPTIONS (3 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @PostMapping("/{questionId}/options")
    @Operation(summary = "Tambah opsi ke soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<OptionResponse>> addOption(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @Valid @RequestBody CreateOptionRequest request) {
        OptionResponse option = questionService.addOption(tenantId, questionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Opsi berhasil ditambahkan", option));
    }

    @PutMapping("/{questionId}/options/{optionId}")
    @Operation(summary = "Update opsi")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<OptionResponse>> updateOption(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @PathVariable UUID optionId,
            @Valid @RequestBody UpdateOptionRequest request) {
        OptionResponse option = questionService.updateOption(tenantId, questionId, optionId, request);
        return ResponseEntity.ok(ApiResponse.success("Opsi berhasil diperbarui", option));
    }

    @DeleteMapping("/{questionId}/options/{optionId}")
    @Operation(summary = "Hapus opsi")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @PathVariable UUID optionId) {
        questionService.deleteOption(tenantId, questionId, optionId);
        return ResponseEntity.ok(ApiResponse.success("Opsi berhasil dihapus", null));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  ATTACHMENTS (2 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @PostMapping("/{questionId}/attachments")
    @Operation(summary = "Tambah lampiran ke soal (metadata saja, upload file terpisah)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttachmentResponse>> addAttachment(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @RequestParam String fileName,
            @RequestParam String filePath,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) Integer fileSize) {
        AttachmentResponse attachment = questionService.addAttachment(
                tenantId, questionId, fileName, filePath, fileType, fileSize);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lampiran berhasil ditambahkan", attachment));
    }

    @DeleteMapping("/{questionId}/attachments/{attachmentId}")
    @Operation(summary = "Hapus lampiran")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @PathVariable UUID attachmentId) {
        questionService.deleteAttachment(tenantId, questionId, attachmentId);
        return ResponseEntity.ok(ApiResponse.success("Lampiran berhasil dihapus", null));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  MEDIA (3 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping("/{questionId}/media")
    @Operation(summary = "Daftar media yang terkait dengan soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<MediaResponse>>> listQuestionMedia(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId) {
        List<MediaResponse> media = questionService.getMediaForQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success("Media soal berhasil diambil", media));
    }

    @PostMapping("/{questionId}/media")
    @Operation(summary = "Lampirkan media ke soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> attachMedia(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @RequestBody List<UUID> mediaIds) {
        questionService.attachMedia(tenantId, questionId, mediaIds);
        return ResponseEntity.ok(ApiResponse.success("Media berhasil dilampirkan ke soal", null));
    }

    @DeleteMapping("/{questionId}/media/{mediaId}")
    @Operation(summary = "Lepaskan media dari soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> detachMedia(
            @RequestParam UUID tenantId,
            @PathVariable UUID questionId,
            @PathVariable UUID mediaId) {
        questionService.detachMedia(tenantId, questionId, mediaId);
        return ResponseEntity.ok(ApiResponse.success("Media berhasil dilepas dari soal", null));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  FOLDERS (4 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping("/folders")
    @Operation(summary = "Tree folder bank soal per tenant")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getFolderTree(
            @RequestParam UUID tenantId) {
        List<FolderResponse> tree = folderService.getTree(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Folder berhasil diambil", tree));
    }

    @PostMapping("/folders")
    @Operation(summary = "Buat folder baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<FolderResponse>> createFolder(
            @Valid @RequestBody CreateFolderRequest request) {
        FolderResponse folder = folderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Folder berhasil dibuat", folder));
    }

    @PutMapping("/folders/{id}")
    @Operation(summary = "Update folder")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<FolderResponse>> updateFolder(
            @RequestParam UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFolderRequest request) {
        FolderResponse folder = folderService.update(tenantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Folder berhasil diperbarui", folder));
    }

    @DeleteMapping("/folders/{id}")
    @Operation(summary = "Hapus folder")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        folderService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Folder berhasil dihapus", null));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  CATEGORIES (4 endpoints)
    // ══════════════════════════════════════════════════════════════════════════════

    @GetMapping("/categories")
    @Operation(summary = "Daftar kategori soal per tenant")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<QuestionCategoryResponse>>> listCategories(
            @RequestParam UUID tenantId) {
        List<QuestionCategoryResponse> categories = categoryService.listByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Kategori soal berhasil diambil", categories));
    }

    @PostMapping("/categories")
    @Operation(summary = "Buat kategori soal baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<QuestionCategoryResponse>> createCategory(
            @Valid @RequestBody CreateQuestionCategoryRequest request) {
        QuestionCategoryResponse category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kategori soal berhasil dibuat", category));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update kategori soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<QuestionCategoryResponse>> updateCategory(
            @RequestParam UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQuestionCategoryRequest request) {
        QuestionCategoryResponse category = categoryService.update(tenantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Kategori soal berhasil diperbarui", category));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Hapus kategori soal")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @RequestParam UUID tenantId,
            @PathVariable UUID id) {
        categoryService.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.success("Kategori soal berhasil dihapus", null));
    }
}
