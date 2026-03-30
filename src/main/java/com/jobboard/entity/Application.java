package com.jobboard.entity;

import com.jobboard.enums.ApplicationStatus;
import com.jobboard.enums.MatchLevel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_application_job_candidate",
                        columnNames = {"job_id", "candidate_id"}
                )
        },
        indexes = {
                @Index(name = "idx_applications_job", columnList = "job_id"),
                @Index(name = "idx_applications_candidate", columnList = "candidate_id"),
                @Index(name = "idx_applications_status", columnList = "status"),
                @Index(name = "idx_applications_applied", columnList = "applied_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_applications_job"))
    private Job job;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_applications_candidate"))
    private User candidate;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "resume_url", length = 512)
    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "employer_notes", columnDefinition = "TEXT")
    private String employerNotes;

    @Column(name = "application_score")
    private Integer applicationScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_level", length = 10)
    private MatchLevel matchLevel;

    @CreatedDate
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}