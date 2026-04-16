package com.tujuhsembilan.smartedutelu.domain.question.entity;

import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
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
@Table(name = "question_media", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"question_id", "media_file_id"})
})
public class QuestionMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", nullable = false)
    private MediaFile mediaFile;

    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;
}
