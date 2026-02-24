package com.company.ppm.service;

import com.company.ppm.domain.entity.KpiSummary;
import com.company.ppm.events.KpiAggregateEvent;
import com.company.ppm.repository.KpiRepository;
import com.company.ppm.repository.KpiSummaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KpiSummaryMaterializer {

    private final KpiSummaryRepository kpiSummaryRepository;
    private final KpiRepository kpiRepository;
    private final ObjectMapper objectMapper;

    public KpiSummaryMaterializer(
            KpiSummaryRepository kpiSummaryRepository,
            KpiRepository kpiRepository,
            ObjectMapper objectMapper
    ) {
        this.kpiSummaryRepository = kpiSummaryRepository;
        this.kpiRepository = kpiRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.kpi-aggregates}", groupId = "ppm-kpi-materializer")
    @Transactional
    public void materialize(String payload) {
        try {
            KpiAggregateEvent aggregate = objectMapper.readValue(payload, KpiAggregateEvent.class);
            if (aggregate.getKpiId() == null) {
                return;
            }

            var kpi = kpiRepository.findById(aggregate.getKpiId()).orElse(null);
            if (kpi == null) {
                return;
            }

            KpiSummary summary = kpiSummaryRepository.findByKpiId(kpi.getId()).orElseGet(() -> {
                KpiSummary created = new KpiSummary();
                created.setKpi(kpi);
                return created;
            });

            summary.setReadingCount(aggregate.getCount());
            summary.setSumValue(aggregate.getSum());
            summary.setMinValue(aggregate.getMin());
            summary.setMaxValue(aggregate.getMax());
            summary.setAvgValue(aggregate.getAverage());
            summary.setLastValue(aggregate.getLastValue());
            summary.setLastObservedAt(aggregate.getLastObservedAt());

            kpiSummaryRepository.save(summary);
        } catch (Exception ignored) {
            // Ignore malformed events and continue stream materialization.
        }
    }
}
