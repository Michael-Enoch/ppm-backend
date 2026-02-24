package com.company.ppm.repository;

import com.company.ppm.domain.entity.KpiSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiSummaryRepository extends JpaRepository<KpiSummary, Long> {
    Optional<KpiSummary> findByKpiId(Long kpiId);
}
