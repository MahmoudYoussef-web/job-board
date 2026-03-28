package com.jobboard.entity;

import com.jobboard.enums.ExperienceLevel;
import com.jobboard.enums.JobStatus;
import com.jobboard.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a job posting created by an EMPLOYER.
 */
@Entity
@Table(
    name = "jobs",
    indexes = {
        @Index(name = "idx_jobs_title",    columnList = "title"),
        @Index(name = "idx_jobs_location", columnList = "location"),
        @Index(name = "idx_jobs_salary",   columnList = "salary_min, salary_max"),
        @Index(name = "idx_jobs_status",   columnList = "status"),
        @Index(name = "idx_jobs_job_type", columnList = "job_type"),
        @Index(name = "idx_jobs_employer", columnList = "employer_id"),
        @Index(name = "idx_jobs_created",  columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employer_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_jobs_employer"))
    private User employer;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 20)
    private JobType jobType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 20)
    private ExperienceLevel experienceLevel;

    @Column(name = "salary_min", precision = 15, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 15, scale = 2)
    private BigDecimal salaryMax;

    @Size(max = 10)
    @Column(name = "salary_currency", length = 10)
    @Builder.Default
    private String salaryCurrency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    @Column
    private LocalDate deadline;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------------
    // Relationships
    // ----------------------------------------------------------------

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}
