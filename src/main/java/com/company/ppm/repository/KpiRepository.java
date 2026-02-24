package com.company.ppm.repository;

import com.company.ppm.domain.entity.Kpi;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findByOrganizationId(Long organizationId);
}
