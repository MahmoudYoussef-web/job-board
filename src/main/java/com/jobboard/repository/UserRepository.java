package com.jobboard.repository;

import com.jobboard.entity.User;
import com.jobboard.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Used by Spring Security UserDetailsService to load a user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Registration guard — prevent duplicate emails.
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by role with pagination (admin feature — Phase 2).
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Find active users only.
     */
    Page<User> findByRoleAndIsActive(Role role, boolean isActive, Pageable pageable);

    /**
     * Used in employer dashboard — fetch employer with posted jobs in one query.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.postedJobs WHERE u.id = :id")
    Optional<User> findByIdWithPostedJobs(@Param("id") Long id);
}
