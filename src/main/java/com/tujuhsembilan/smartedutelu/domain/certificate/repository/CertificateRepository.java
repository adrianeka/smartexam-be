package com.tujuhsembilan.smartedutelu.domain.certificate.repository;

import com.tujuhsembilan.smartedutelu.domain.certificate.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    Page<Certificate> findByUserId(UUID userId, Pageable pageable);

    Page<Certificate> findByExamId(UUID examId, Pageable pageable);

    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    boolean existsByCertificateNumber(String certificateNumber);
}
