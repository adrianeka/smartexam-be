package com.tujuhsembilan.smartedutelu.domain.exam.entity;

import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "exam_sections")
public class ExamSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String instruction;

    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "question_count", nullable = false)
    @Builder.Default
    private Integer questionCount = 0;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<ExamQuestion> examQuestions = new ArrayList<>();
}
