package com.company.ppm.events;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KpiReadingEvent {
    private Long readingId;
    private Long kpiId;
    private Long organizationId;
    private BigDecimal value;
    private Instant observedAt;
    private String source;
}
