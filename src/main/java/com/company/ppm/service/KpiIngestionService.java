package com.company.ppm.service;

import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.config.AppProperties;
import com.company.ppm.domain.entity.Kpi;
import com.company.ppm.domain.entity.KpiReading;
import com.company.ppm.dto.KpiReadingRequest;
import com.company.ppm.dto.KpiReadingResponse;
import com.company.ppm.dto.KpiReadingUpdateRequest;
import com.company.ppm.events.KpiReadingEvent;
import com.company.ppm.repository.KpiReadingRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KpiIngestionService {

    private final KpiService kpiService;
    private final KpiReadingRepository kpiReadingRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppProperties appProperties;

    public KpiIngestionService(
            KpiService kpiService,
            KpiReadingRepository kpiReadingRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            AppProperties appProperties
    ) {
        this.kpiService = kpiService;
        this.kpiReadingRepository = kpiReadingRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.appProperties = appProperties;
    }

    @Transactional
    public KpiReadingResponse ingest(Long kpiId, KpiReadingRequest request) {
        Kpi kpi = kpiService.requireAccessible(kpiId);

        KpiReading reading = new KpiReading();
        reading.setKpi(kpi);
        reading.setValue(request.value());
        reading.setSource(request.source());
        reading.setObservedAt(request.observedAt() == null ? Instant.now() : request.observedAt());

        KpiReading saved = kpiReadingRepository.save(reading);

        KpiReadingEvent event = new KpiReadingEvent();
        event.setReadingId(saved.getId());
        event.setKpiId(saved.getKpi().getId());
        event.setOrganizationId(saved.getKpi().getOrganization().getId());
        event.setValue(saved.getValue());
        event.setObservedAt(saved.getObservedAt());
        event.setSource(saved.getSource());

        kafkaTemplate.send(appProperties.getKafka().getTopics().getKpiReadings(), String.valueOf(kpiId), event);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<KpiReadingResponse> listReadings(Long kpiId) {
        kpiService.requireAccessible(kpiId);
        return kpiReadingRepository.findByKpiIdOrderByObservedAtDesc(kpiId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public KpiReadingResponse getReading(Long kpiId, Long readingId) {
        kpiService.requireAccessible(kpiId);
        KpiReading reading = kpiReadingRepository.findByIdAndKpiId(readingId, kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI reading not found: " + readingId));
        return toResponse(reading);
    }

    @Transactional
    public KpiReadingResponse updateReading(Long kpiId, Long readingId, KpiReadingUpdateRequest request) {
        kpiService.requireAccessible(kpiId);
        KpiReading reading = kpiReadingRepository.findByIdAndKpiId(readingId, kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI reading not found: " + readingId));

        if (request.value() != null) {
            reading.setValue(request.value());
        }
        if (request.observedAt() != null) {
            reading.setObservedAt(request.observedAt());
        }
        if (request.source() != null) {
            reading.setSource(request.source());
        }

        return toResponse(kpiReadingRepository.save(reading));
    }

    @Transactional
    public void deleteReading(Long kpiId, Long readingId) {
        kpiService.requireAccessible(kpiId);
        KpiReading reading = kpiReadingRepository.findByIdAndKpiId(readingId, kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI reading not found: " + readingId));
        kpiReadingRepository.delete(reading);
    }

    private KpiReadingResponse toResponse(KpiReading reading) {
        return new KpiReadingResponse(
                reading.getId(),
                reading.getKpi().getId(),
                reading.getValue(),
                reading.getObservedAt(),
                reading.getSource(),
                reading.getCreatedAt()
        );
    }
}
