package com.tujuhsembilan.smartedutelu.domain.analytics.entity;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "exam_analytics")
public class ExamAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false, unique = true)
    private Exam exam;

    @Column(name = "total_participants", nullable = false)
    @Builder.Default
    private Integer totalParticipants = 0;

    @Column(name = "total_completions", nullable = false)
    @Builder.Default
    private Integer totalCompletions = 0;

    @Column(name = "avg_score", precision = 10, scale = 2)
    private BigDecimal avgScore;

    @Column(name = "pass_rate", precision = 5, scale = 2)
    private BigDecimal passRate;

    @Column(name = "difficulty_index", precision = 5, scale = 4)
    private BigDecimal difficultyIndex;

    @Column(name = "discrimination_index", precision = 5, scale = 4)
    private BigDecimal discriminationIndex;

    @Column(name = "calculated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime calculatedAt = OffsetDateTime.now();
}
