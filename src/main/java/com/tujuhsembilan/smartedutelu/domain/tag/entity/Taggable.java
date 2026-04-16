package com.tujuhsembilan.smartedutelu.domain.tag.entity;

import com.tujuhsembilan.smartedutelu.domain.tag.enums.TaggableType;
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
@Table(name = "taggables", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tag_id", "taggable_type", "taggable_id"})
})
public class Taggable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "taggable_type", nullable = false, length = 100)
    private TaggableType taggableType;

    @Column(name = "taggable_id", nullable = false)
    private UUID taggableId;
}
