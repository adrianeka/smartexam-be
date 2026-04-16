package com.tujuhsembilan.smartedutelu.domain.tenant.repository;

import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByDomain(String domain);

    boolean existsByDomain(String domain);

    boolean existsByName(String name);
}
