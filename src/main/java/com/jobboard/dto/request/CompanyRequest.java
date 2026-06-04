package com.jobboard.dto.request;

import com.jobboard.enums.CompanySize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 100)
    private String industry;

    private CompanySize size;

    private String description;

    @Size(max = 255)
    private String website;

    @Size(max = 512)
    private String logoUrl;

    @Size(max = 200)
    private String location;

    private Integer foundedYear;
}
