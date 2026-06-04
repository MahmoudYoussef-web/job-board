package com.jobboard.repository;

import com.jobboard.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Company> findByOwnerId(Long ownerId);
}
