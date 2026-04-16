package com.tujuhsembilan.smartedutelu.domain.certificate.entity;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "certificates", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "exam_id"})
})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private CertificateTemplate template;

    @Column(name = "certificate_number", nullable = false, unique = true, length = 100)
    private String certificateNumber;

    @Column(name = "certificate_url", columnDefinition = "TEXT")
    private String certificateUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "issued_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime issuedAt = OffsetDateTime.now();
}
