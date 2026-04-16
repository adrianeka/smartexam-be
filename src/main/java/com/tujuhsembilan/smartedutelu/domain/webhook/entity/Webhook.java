package com.tujuhsembilan.smartedutelu.domain.webhook.entity;

import com.tujuhsembilan.smartedutelu.common.config.EncryptedStringConverter;
import com.tujuhsembilan.smartedutelu.common.config.StringListConverter;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "tenant")
@Entity
@Table(name = "webhooks")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    /** Secret disimpan terenkripsi (AES-256-GCM). */
    @Convert(converter = EncryptedStringConverter.class)
    @Column(length = 512)
    private String secret;

    /** I6: Daftar event yang di-subscribe, disimpan sebagai JSON array di kolom TEXT. */
    @Convert(converter = StringListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    @Builder.Default
    private List<String> events = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
