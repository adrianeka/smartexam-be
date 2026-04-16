package com.tujuhsembilan.smartedutelu.domain.scheduling.entity;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "proctor_assignments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "proctor_id"})
})
public class ProctorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ExamSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proctor_id", nullable = false)
    private User proctor;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String role = "observer";

    @Column(name = "assigned_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime assignedAt = OffsetDateTime.now();
}
