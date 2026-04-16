package com.tujuhsembilan.smartedutelu.domain.certificate.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.CreateTemplateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.UpdateTemplateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.response.TemplateResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.service.CertificateTemplateService;
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
@RequestMapping("/v1/certificate-templates")
@RequiredArgsConstructor
@Tag(name = "Certificate Templates", description = "Template sertifikat")
public class CertificateTemplateController {

    private final CertificateTemplateService templateService;

    @GetMapping
    @Operation(summary = "Daftar template sertifikat")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<TemplateResponse>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(templateService.listTemplates(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail template sertifikat")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<TemplateResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(templateService.getTemplate(id)));
    }

    @PostMapping
    @Operation(summary = "Buat template sertifikat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemplateResponse>> create(@Valid @RequestBody CreateTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(templateService.createTemplate(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update template sertifikat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemplateResponse>> update(@PathVariable UUID id, @Valid @RequestBody UpdateTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(templateService.updateTemplate(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus template sertifikat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
