package com.tujuhsembilan.smartedutelu.domain.scheduling.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
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
@Table(name = "cheating_logs")
public class CheatingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ExamSession session;

    @Column(nullable = false, length = 100)
    private String event;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "low";

    @Column(name = "screenshot_url", columnDefinition = "TEXT")
    private String screenshotUrl;

    @CreationTimestamp
    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;
}