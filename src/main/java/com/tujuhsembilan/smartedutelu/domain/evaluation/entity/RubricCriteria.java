package com.tujuhsembilan.smartedutelu.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"rubric"})
@Entity
@Table(name = "rubric_criteria")
public class RubricCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id", nullable = false)
    private GradingRubric rubric;

    @Column(nullable = false)
    private String criterion;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxScore;

    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;
}
