package com.company.ppm.service;

import com.company.ppm.domain.entity.ReportJob;
import com.company.ppm.domain.enums.ReportFormat;
import com.company.ppm.domain.enums.ReportStatus;
import com.company.ppm.repository.ReportJobRepository;
import com.company.ppm.storage.ObjectStorageService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportWorker {

    private final ReportJobRepository reportJobRepository;
    private final ObjectStorageService objectStorageService;

    public ReportWorker(ReportJobRepository reportJobRepository, ObjectStorageService objectStorageService) {
        this.reportJobRepository = reportJobRepository;
        this.objectStorageService = objectStorageService;
    }

    @Async
    @Transactional
    public void generate(Long reportId) {
        ReportJob job = reportJobRepository.findById(reportId).orElseThrow();
        job.setStatus(ReportStatus.PROCESSING);
        reportJobRepository.save(job);

        try {
            String extension = job.getFormat().name().toLowerCase();
            String objectKey = "reports/" + job.getId() + "." + extension;

            byte[] bytes = job.getFormat() == ReportFormat.CSV
                    ? generateCsv(job)
                    : generatePdf(job);
            String contentType = job.getFormat() == ReportFormat.CSV ? "text/csv" : "application/pdf";

            objectStorageService.upload(objectKey, bytes, contentType);
            String downloadUrl = objectStorageService.generateDownloadUrl(objectKey);

            job.setObjectKey(objectKey);
            job.setDownloadUrl(downloadUrl);
            job.setStatus(ReportStatus.READY);
            job.setErrorMessage(null);
            reportJobRepository.save(job);
        } catch (Exception ex) {
            job.setStatus(ReportStatus.FAILED);
            job.setErrorMessage(ex.getMessage());
            reportJobRepository.save(job);
        }
    }

    private byte[] generateCsv(ReportJob job) {
        StringBuilder sb = new StringBuilder();
        sb.append("report_id,format,status\n");
        sb.append(job.getId()).append(',').append(job.getFormat()).append(',').append(job.getStatus()).append('\n');
        sb.append("parameters,\"").append(job.getRequestPayload() == null ? "{}" : job.getRequestPayload().replace("\"", "\"\""))
                .append("\"\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generatePdf(ReportJob job) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(50, 700);
                content.showText("Enterprise PPM Report");
                content.newLineAtOffset(0, -20);
                content.showText("Report ID: " + job.getId());
                content.newLineAtOffset(0, -20);
                content.showText("Requested payload: " + trim(job.getRequestPayload()));
                content.endText();
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    private String trim(String payload) {
        if (payload == null) {
            return "{}";
        }
        return payload.length() <= 120 ? payload : payload.substring(0, 120);
    }
}
