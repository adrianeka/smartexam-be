package com.tujuhsembilan.smartedutelu.domain.exam.entity;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
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
@Entity
@Table(name = "exams", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "slug"})
})
@SQLRestriction("deleted_at IS NULL")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ExamCategory category;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "exam_type", nullable = false, length = 100)
    @Builder.Default
    private String examType = "standard";

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "max_attempts", nullable = false)
    @Builder.Default
    private Integer maxAttempts = 1;

    @Column(name = "pass_percentage", nullable = false)
    @Builder.Default
    private Integer passPercentage = 60;

    @Column(name = "total_score", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalScore = new BigDecimal("100");

    @Column(name = "random_questions", nullable = false)
    @Builder.Default
    private Boolean randomQuestions = false;

    @Column(name = "random_answers", nullable = false)
    @Builder.Default
    private Boolean randomAnswers = false;

    @Column(name = "show_result_mode", nullable = false, length = 50)
    @Builder.Default
    private String showResultMode = "after_submit";

    @Column(name = "allow_review", nullable = false)
    @Builder.Default
    private Boolean allowReview = true;

    @Column(name = "shuffle_sections", nullable = false)
    @Builder.Default
    private Boolean shuffleSections = false;

    @Column(name = "require_proctoring", nullable = false)
    @Builder.Default
    private Boolean requireProctoring = false;

    @Column(name = "feedback_type", nullable = false, length = 50)
    @Builder.Default
    private String feedbackType = "summary";

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "draft";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<ExamSection> sections = new ArrayList<>();

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
