package com.jobboard.dto.response;

import com.jobboard.enums.CompanySize;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompanyResponse {

    private Long id;
    private String name;
    private String industry;
    private CompanySize size;
    private String description;
    private String website;
    private String logoUrl;
    private String location;
    private Integer foundedYear;
    private boolean isVerified;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
}
