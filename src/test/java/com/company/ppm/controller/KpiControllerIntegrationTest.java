package com.company.ppm.controller;

import com.company.ppm.dto.KpiReadingRequest;
import com.company.ppm.dto.KpiReadingResponse;
import com.company.ppm.security.JwtAuthenticationFilter;
import com.company.ppm.security.KpiIngestionRateLimitFilter;
import com.company.ppm.service.KpiIngestionService;
import com.company.ppm.service.KpiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KpiController.class)
@AutoConfigureMockMvc(addFilters = false)
class KpiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KpiService kpiService;

    @MockitoBean
    private KpiIngestionService kpiIngestionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private KpiIngestionRateLimitFilter kpiIngestionRateLimitFilter;

    @Test
    void ingestReadingReturnsSavedReading() throws Exception {
        KpiReadingResponse response = new KpiReadingResponse(
                5L,
                1L,
                new BigDecimal("99.50"),
                Instant.parse("2026-02-24T10:00:00Z"),
                "system",
                Instant.parse("2026-02-24T10:00:01Z")
        );

        when(kpiIngestionService.ingest(any(Long.class), any(KpiReadingRequest.class))).thenReturn(response);

        KpiReadingRequest request = new KpiReadingRequest(new BigDecimal("99.50"), Instant.parse("2026-02-24T10:00:00Z"), "system");

        mockMvc.perform(post("/api/kpis/1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.value").value(99.5));
    }
}
