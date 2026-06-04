package com.jobboard.service;

import com.jobboard.dto.request.CompanyRequest;
import com.jobboard.dto.response.CompanyResponse;
import com.jobboard.entity.Company;
import com.jobboard.entity.User;
import com.jobboard.exception.ForbiddenOperationException;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    public Page<CompanyResponse> searchCompanies(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return companyRepository.findAll(pageable).map(this::toResponse);
        }
        return companyRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponse);
    }

    public CompanyResponse getById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        return toResponse(company);
    }

    @Transactional
    public CompanyResponse create(Long ownerId, CompanyRequest request) {
        User owner = userService.findById(ownerId);

        Company company = Company.builder()
                .name(request.getName())
                .industry(request.getIndustry())
                .size(request.getSize())
                .description(request.getDescription())
                .website(request.getWebsite())
                .logoUrl(request.getLogoUrl())
                .location(request.getLocation())
                .foundedYear(request.getFoundedYear())
                .owner(owner)
                .build();

        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse update(Long ownerId, Long companyId, CompanyRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        if (!company.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Not your company");
        }

        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setSize(request.getSize());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLogoUrl(request.getLogoUrl());
        company.setLocation(request.getLocation());
        company.setFoundedYear(request.getFoundedYear());

        return toResponse(companyRepository.save(company));
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .industry(company.getIndustry())
                .size(company.getSize())
                .description(company.getDescription())
                .website(company.getWebsite())
                .logoUrl(company.getLogoUrl())
                .location(company.getLocation())
                .foundedYear(company.getFoundedYear())
                .isVerified(company.isVerified())
                .ownerId(company.getOwner().getId())
                .ownerName(company.getOwner().getFullName())
                .createdAt(company.getCreatedAt())
                .build();
    }
}
