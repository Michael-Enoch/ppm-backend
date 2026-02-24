package com.company.ppm.events;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KpiAggregateEvent {
    private Long kpiId;
    private long count;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal average;
    private BigDecimal lastValue;
    private Instant lastObservedAt;

    public void apply(KpiReadingEvent reading) {
        if (reading == null || reading.getValue() == null) {
            return;
        }
        if (kpiId == null) {
            kpiId = reading.getKpiId();
        }

        count += 1;
        sum = sum.add(reading.getValue());
        min = min == null ? reading.getValue() : min.min(reading.getValue());
        max = max == null ? reading.getValue() : max.max(reading.getValue());
        average = sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
        lastValue = reading.getValue();
        lastObservedAt = reading.getObservedAt();
    }
}
