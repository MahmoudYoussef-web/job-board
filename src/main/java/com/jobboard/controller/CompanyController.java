package com.jobboard.controller;

import com.jobboard.dto.request.CompanyRequest;
import com.jobboard.dto.response.CompanyResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<Page<CompanyResponse>> searchCompanies(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        return ResponseEntity.ok(companyService.searchCompanies(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.create(principal.getId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(companyService.update(principal.getId(), id, request));
    }
}
