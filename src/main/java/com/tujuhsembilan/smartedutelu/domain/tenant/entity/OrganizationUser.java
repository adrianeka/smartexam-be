package com.tujuhsembilan.smartedutelu.domain.tenant.entity;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "organization_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "user_id"})
})
public class OrganizationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String role = "member";
}
