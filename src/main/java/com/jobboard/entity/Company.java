package com.jobboard.entity;

import com.jobboard.enums.CompanySize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @Size(max = 100)
    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private CompanySize size;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(length = 255)
    private String website;

    @Size(max = 512)
    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Size(max = 200)
    @Column(length = 200)
    private String location;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
