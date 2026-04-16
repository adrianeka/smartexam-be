package com.tujuhsembilan.smartedutelu.domain.certificate.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.IssueCertificateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.response.CertificateResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.service.CertificateService;
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
@RequestMapping("/v1/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Sertifikat ujian")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    @Operation(summary = "Daftar sertifikat (filter: userId, examId)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<CertificateResponse>>> list(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID examId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(certificateService.listCertificates(userId, examId, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail sertifikat")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<CertificateResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(certificateService.getCertificate(id)));
    }

    @GetMapping("/verify/{certificateNumber}")
    @Operation(summary = "Verifikasi sertifikat berdasarkan nomor")
    public ResponseEntity<ApiResponse<CertificateResponse>> verify(@PathVariable String certificateNumber) {
        return ResponseEntity.ok(ApiResponse.success(certificateService.verifyCertificate(certificateNumber)));
    }

    @PostMapping
    @Operation(summary = "Terbitkan sertifikat untuk siswa")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CertificateResponse>> issue(@Valid @RequestBody IssueCertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(certificateService.issueCertificate(request)));
    }
}
