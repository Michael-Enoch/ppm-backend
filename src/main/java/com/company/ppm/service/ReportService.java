package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.common.exception.UnauthorizedException;
import com.company.ppm.domain.entity.ReportJob;
import com.company.ppm.domain.enums.ReportStatus;
import com.company.ppm.dto.ReportRequest;
import com.company.ppm.dto.ReportResponse;
import com.company.ppm.repository.ReportJobRepository;
import com.company.ppm.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportJobRepository reportJobRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ReportWorker reportWorker;
    private final CurrentUserService currentUserService;

    public ReportService(
            ReportJobRepository reportJobRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            ReportWorker reportWorker,
            CurrentUserService currentUserService
    ) {
        this.reportJobRepository = reportJobRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.reportWorker = reportWorker;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ReportResponse requestReport(ReportRequest request, String principalEmail) {
        var requester = userRepository.findByEmail(principalEmail)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        ReportJob job = new ReportJob();
        job.setRequestedBy(requester);
        job.setFormat(request.format());
        job.setStatus(ReportStatus.PENDING);
        job.setRequestPayload(serializePayload(request));

        ReportJob saved = reportJobRepository.save(job);
        reportWorker.generate(saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public String getDownloadLink(Long reportId, String principalEmail) {
        ReportJob report = reportJobRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        if (!currentUserService.isAdmin() && !report.getRequestedBy().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Report not found: " + reportId);
        }

        if (report.getStatus() != ReportStatus.READY || report.getDownloadUrl() == null) {
            throw new BadRequestException("Report not ready yet");
        }
        return report.getDownloadUrl();
    }

    private String serializePayload(ReportRequest request) {
        try {
            return objectMapper.writeValueAsString(request.parameters());
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private ReportResponse toResponse(ReportJob reportJob) {
        return new ReportResponse(
                reportJob.getId(),
                reportJob.getFormat(),
                reportJob.getStatus(),
                reportJob.getDownloadUrl(),
                reportJob.getErrorMessage(),
                reportJob.getCreatedAt(),
                reportJob.getUpdatedAt()
        );
    }
}
