package com.jobboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // AuditorAware<Long> bean (current user ID) will be added in Phase 2
    // when authentication context is available at service layer.
}
