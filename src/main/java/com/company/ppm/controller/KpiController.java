package com.company.ppm.controller;

import com.company.ppm.dto.KpiCreateRequest;
import com.company.ppm.dto.KpiReadingRequest;
import com.company.ppm.dto.KpiReadingResponse;
import com.company.ppm.dto.KpiResponse;
import com.company.ppm.dto.KpiUpdateRequest;
import com.company.ppm.dto.KpiReadingUpdateRequest;
import com.company.ppm.service.KpiIngestionService;
import com.company.ppm.service.KpiService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;
    private final KpiIngestionService kpiIngestionService;

    public KpiController(KpiService kpiService, KpiIngestionService kpiIngestionService) {
        this.kpiService = kpiService;
        this.kpiIngestionService = kpiIngestionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public List<KpiResponse> listKpis() {
        return kpiService.list();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public KpiResponse createKpi(@Valid @RequestBody KpiCreateRequest request) {
        return kpiService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public KpiResponse getKpi(@PathVariable Long id) {
        return kpiService.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public KpiResponse updateKpi(@PathVariable Long id, @RequestBody KpiUpdateRequest request) {
        return kpiService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public void deleteKpi(@PathVariable Long id) {
        kpiService.delete(id);
    }

    @PostMapping("/{id}/readings")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER')")
    public KpiReadingResponse ingestReading(@PathVariable Long id, @Valid @RequestBody KpiReadingRequest request) {
        return kpiIngestionService.ingest(id, request);
    }

    @GetMapping("/{id}/readings")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public List<KpiReadingResponse> listReadings(@PathVariable Long id) {
        return kpiIngestionService.listReadings(id);
    }

    @GetMapping("/{id}/readings/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public KpiReadingResponse getReading(@PathVariable Long id, @PathVariable Long readingId) {
        return kpiIngestionService.getReading(id, readingId);
    }

    @PatchMapping("/{id}/readings/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public KpiReadingResponse updateReading(
            @PathVariable Long id,
            @PathVariable Long readingId,
            @RequestBody KpiReadingUpdateRequest request
    ) {
        return kpiIngestionService.updateReading(id, readingId, request);
    }

    @DeleteMapping("/{id}/readings/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public void deleteReading(@PathVariable Long id, @PathVariable Long readingId) {
        kpiIngestionService.deleteReading(id, readingId);
    }
}
