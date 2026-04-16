package com.tujuhsembilan.smartedutelu.domain.certificate.repository;

import com.tujuhsembilan.smartedutelu.domain.certificate.entity.CertificateTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, UUID> {

    Page<CertificateTemplate> findAll(Pageable pageable);

    Optional<CertificateTemplate> findByIsDefaultTrue();
}
