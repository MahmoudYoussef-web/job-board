package com.jobboard.entity;

import com.jobboard.enums.InterviewResult;
import com.jobboard.enums.InterviewStatus;
import com.jobboard.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private User interviewer;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "duration_minutes")
    @Builder.Default
    private Integer durationMinutes = 60;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(name = "meeting_platform", length = 20)
    private String meetingPlatform;

    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InterviewType type;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private InterviewResult result;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
