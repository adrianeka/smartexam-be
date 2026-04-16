package com.tujuhsembilan.smartedutelu.domain.tenant.repository;

import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    List<Organization> findByTenantId(UUID tenantId);

    Optional<Organization> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByNameAndTenantId(String name, UUID tenantId);
}
