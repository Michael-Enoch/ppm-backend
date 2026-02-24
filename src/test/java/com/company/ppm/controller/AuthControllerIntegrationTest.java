package com.company.ppm.controller;

import com.company.ppm.auth.AuthController;
import com.company.ppm.auth.AuthService;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.domain.entity.Role;
import com.company.ppm.domain.enums.RoleName;
import com.company.ppm.security.JwtAuthenticationFilter;
import com.company.ppm.security.KpiIngestionRateLimitFilter;
import com.company.ppm.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private KpiIngestionRateLimitFilter kpiIngestionRateLimitFilter;

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void meReturnsCurrentUserProfile() throws Exception {
        Organization organization = new Organization();
        organization.setId(1L);

        Role role = new Role();
        role.setName(RoleName.ADMIN);

        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("admin@example.com");
        user.setFullName("System Admin");
        user.setActive(true);
        user.setOrganization(organization);
        user.setRoles(Set.of(role));

        when(currentUserService.currentUser()).thenReturn(user);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.organizationId").value(1L))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }
}
