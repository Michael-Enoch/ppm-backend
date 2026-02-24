package com.company.ppm.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "kpi_summaries")
public class KpiSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false, unique = true)
    private Kpi kpi;

    @Column(name = "reading_count", nullable = false)
    private Long readingCount = 0L;

    @Column(name = "sum_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal sumValue = BigDecimal.ZERO;

    @Column(name = "min_value", precision = 19, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 19, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "avg_value", precision = 19, scale = 4)
    private BigDecimal avgValue;

    @Column(name = "last_value", precision = 19, scale = 4)
    private BigDecimal lastValue;

    @Column(name = "last_observed_at")
    private Instant lastObservedAt;
}
