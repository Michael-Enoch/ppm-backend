package com.company.ppm.controller;

import com.company.ppm.dto.ReportRequest;
import com.company.ppm.dto.ReportResponse;
import com.company.ppm.service.ReportService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public ReportResponse createReport(@Valid @RequestBody ReportRequest request, Principal principal) {
        return reportService.requestReport(request, principal.getName());
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public Map<String, String> getDownloadLink(@PathVariable Long id, Principal principal) {
        String link = reportService.getDownloadLink(id, principal.getName());
        return Map.of("downloadUrl", link);
    }
}
