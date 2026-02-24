package com.company.ppm.controller;

import com.company.ppm.dto.DashboardResponse;
import com.company.ppm.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public DashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }
}
