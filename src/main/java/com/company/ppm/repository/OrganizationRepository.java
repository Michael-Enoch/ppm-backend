package com.company.ppm.repository;

import com.company.ppm.domain.entity.Organization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByCode(String code);
}
