package com.tujuhsembilan.smartedutelu.domain.tenant.repository;

import com.tujuhsembilan.smartedutelu.domain.tenant.entity.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, UUID> {

    @Query("SELECT ou FROM OrganizationUser ou JOIN FETCH ou.user WHERE ou.organization.id = :orgId")
    List<OrganizationUser> findByOrganizationIdWithUser(UUID orgId);

    Optional<OrganizationUser> findByOrganizationIdAndUserId(UUID orgId, UUID userId);

    boolean existsByOrganizationIdAndUserId(UUID orgId, UUID userId);
}
