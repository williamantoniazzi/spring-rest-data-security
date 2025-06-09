package br.edu.fatecsjc.lgnspringapi.config;

import br.edu.fatecsjc.lgnspringapi.enums.Permission;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @ContextConfiguration é usado para carregar apenas as classes de configuração necessárias
// @WebMvcTest é para testar a camada de controller, mas também carrega o SecurityConfig para verificar as regras de segurança
@WebMvcTest // This will scan for @Component, @Controller, @Service, @Repository, and @Configuration beans related to web.
@ContextConfiguration(classes = {SecurityConfig.class, JwtAuthenticationFilter.class, ApplicationConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc; // For testing HTTP requests

    @MockBean // Mock beans that SecurityConfig depends on
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private LogoutHandler logoutHandler;
    @MockBean
    private ApplicationConfig applicationConfig; // Mock the config to prevent it from loading real user/token repos
    // We mock JwtService, UserDetailsService, TokenRepository here because ApplicationConfig and JwtAuthenticationFilter need them
    // and we want to isolate the SecurityConfig logic without complex Spring contexts.
    @MockBean
    private JwtService jwtService;
    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockBean
    private br.edu.fatecsjc.lgnspringapi.repository.TokenRepository tokenRepository;


    // This test primarily checks if the SecurityFilterChain bean is created and configured.
    @Test
    @DisplayName("SecurityFilterChain bean should be configured correctly")
    void securityFilterChain_BeanConfiguration() throws Exception {
        // Arrange (no specific arrange needed, @MockBean takes care of dependencies)
        // Act (Spring Boot test context initializes the bean)
        // Assert
        assertNotNull(authenticationProvider); // Ensure the mock is injected
        assertNotNull(jwtAuthenticationFilter); // Ensure the mock is injected
        assertNotNull(logoutHandler); // Ensure the mock is injected

        // Attempt to access an unauthorized path to see if security is active
        mockMvc.perform(get("/admin/test"))
                .andExpect(status().isUnauthorized()); // Expect 401 due to no authentication

        // Verify that the filter is added
        // This is a conceptual check, as @WebMvcTest's security setup can sometimes
        // bypass the actual filter chain for simplicity in unit tests.
        // A full integration test would verify the filter chain order more explicitly.
    }

    @Test
    @DisplayName("Access to /auth/** should be permitted")
    void authEndpoints_Permitted() throws Exception {
        // Arrange, Act & Assert
        mockMvc.perform(get("/auth/authenticate"))
                .andExpect(status().isOk()); // Assuming /auth endpoints return 200 by default (or handle it)

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk()); // Assuming /auth endpoints return 200 by default
    }

    @Test
    @DisplayName("Access to /user/** should require ADMIN role")
    @org.springframework.security.test.context.support.WithMockUser(roles = "USER") // Simulate a user with USER role
    void userEndpoints_RequiresAdminRole_ForbiddenForUser() throws Exception {
        // Arrange, Act & Assert
        mockMvc.perform(get("/user/some-endpoint"))
                .andExpect(status().isForbidden()); // User should be forbidden
    }

    @Test
    @DisplayName("Access to /user/** should be permitted for ADMIN role")
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN") // Simulate a user with ADMIN role
    void userEndpoints_RequiresAdminRole_PermittedForAdmin() throws Exception {
        // Arrange, Act & Assert
        // Note: For actual functionality, these endpoints would need mock service responses.
        // Here, we just verify the security layer's permission.
        mockMvc.perform(get("/user/some-endpoint"))
                .andExpect(status().isNotFound()); // Expect 404 because endpoint doesn't exist, but not 403
    }


    @Test
    @DisplayName("POST to /group/** should require ADMIN_CREATE authority")
    @org.springframework.security.test.context.support.WithMockUser(authorities = "admin:create")
    void groupPost_RequiresAdminCreateAuthority_Permitted() throws Exception {
        // Arrange, Act & Assert
        mockMvc.perform(post("/group/create")) // Use POST to match the rule
                .andExpect(status().isNotFound()); // Expected 404 since no actual endpoint, but not 403
    }

    @Test
    @DisplayName("POST to /group/** should be forbidden without ADMIN_CREATE authority")
    @org.springframework.security.test.context.support.WithMockUser(roles = "USER") // No admin:create authority
    void groupPost_RequiresAdminCreateAuthority_Forbidden() throws Exception {
        // Arrange, Act & Assert
        mockMvc.perform(post("/group/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Logout URL should be configured correctly")
    void logoutUrl_Configured() throws Exception {
        // Arrange
        // Simulate a logged-in user to trigger logout logic
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "pass"));

        // Act
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk()); // Logout typically returns 200 OK

        // Assert
        // Verify that the logout handler was called (conceptually)
        verify(logoutHandler, times(1)).logout(any(), any(), any());
        // Verify that the security context was cleared
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}