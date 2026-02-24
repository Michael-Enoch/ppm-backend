package com.company.ppm.repository;

import com.company.ppm.domain.entity.KpiReading;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiReadingRepository extends JpaRepository<KpiReading, Long> {
    List<KpiReading> findByKpiIdOrderByObservedAtDesc(Long kpiId);
    Optional<KpiReading> findByIdAndKpiId(Long id, Long kpiId);
}
