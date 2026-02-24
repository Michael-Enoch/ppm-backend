package com.company.ppm.controller;

import com.company.ppm.domain.enums.ProjectStatus;
import com.company.ppm.dto.ProjectCreateRequest;
import com.company.ppm.dto.ProjectResponse;
import com.company.ppm.security.JwtAuthenticationFilter;
import com.company.ppm.security.KpiIngestionRateLimitFilter;
import com.company.ppm.service.ProjectService;
import com.company.ppm.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private KpiIngestionRateLimitFilter kpiIngestionRateLimitFilter;

    @Test
    void listProjectsReturnsPayload() throws Exception {
        when(projectService.listProjects()).thenReturn(List.of(new ProjectResponse(
                1L,
                "Migration",
                "Move legacy data",
                ProjectStatus.ACTIVE,
                1L,
                1L,
                Instant.parse("2026-02-24T00:00:00Z"),
                Instant.parse("2026-02-24T00:00:00Z")
        )));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Migration"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void createProjectReturnsCreatedEntity() throws Exception {
        ProjectResponse response = new ProjectResponse(
                2L,
                "Roadmap",
                "Quarterly roadmap",
                ProjectStatus.PLANNED,
                1L,
                1L,
                Instant.parse("2026-02-24T00:00:00Z"),
                Instant.parse("2026-02-24T00:00:00Z")
        );

        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(response);

        ProjectCreateRequest request = new ProjectCreateRequest("Roadmap", "Quarterly roadmap", 1L, ProjectStatus.PLANNED);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Roadmap"));
    }
}
