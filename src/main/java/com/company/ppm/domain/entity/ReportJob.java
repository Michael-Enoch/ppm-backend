package com.company.ppm.domain.entity;

import com.company.ppm.domain.enums.ReportFormat;
import com.company.ppm.domain.enums.ReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "report_jobs")
public class ReportJob extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private AppUser requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportFormat format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "request_payload", length = 4000)
    private String requestPayload;

    @Column(name = "object_key", length = 255)
    private String objectKey;

    @Column(name = "download_url", length = 1000)
    private String downloadUrl;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;
}
