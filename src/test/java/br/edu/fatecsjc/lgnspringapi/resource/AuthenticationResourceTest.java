package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationResponseDTO;
import br.edu.fatecsjc.lgnspringapi.dto.RegisterRequestDTO;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationResource.class)
class AuthenticationResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private RegisterRequestDTO registerRequestDTO;
    private AuthenticationRequestDTO authenticationRequestDTO;
    private AuthenticationResponseDTO authenticationResponseDTO;

    @BeforeEach
    void setUp() {
        registerRequestDTO = RegisterRequestDTO.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        authenticationResponseDTO = AuthenticationResponseDTO.builder()
                .accessToken("mockAccessToken")
                .refreshToken("mockRefreshToken")
                .build();
    }

    @Test
    @DisplayName("Should return 201 Created and tokens on successful registration")
    void register_Success() throws Exception {
        // Arrange
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenReturn(authenticationResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"));

        verify(authenticationService, times(1)).register(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request on registration failure (e.g., duplicate email)")
    void register_Failure() throws Exception {
        // Arrange
        when(authenticationService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new IllegalStateException("Email already registered")); // Example of a service error

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isBadRequest()); // Assuming controller or global handler maps IllegalStateException to 400

        verify(authenticationService, times(1)).register(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 200 OK and tokens on successful authentication")
    void authenticate_Success() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class))).thenReturn(authenticationResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"));

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized on authentication failure")
    void authenticate_Failure() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnauthorized()); // BadCredentialsException typically maps to 401

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 200 OK on successful token refresh")
    void refreshToken_Success() throws Exception {
        // Arrange
        doNothing().when(authenticationService).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // Act & Assert
        mockMvc.perform(post("/auth/refresh-token")
                        .header("Authorization", "Bearer refresh_token_string")) // Simulate refresh token in header
                .andExpect(status().isOk());

        verify(authenticationService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request on refresh token failure (e.g., invalid token)")
    void refreshToken_Failure() throws Exception {
        // Arrange
        doThrow(new IOException("Invalid refresh token")) // Simulate an IO error or service error
                .when(authenticationService).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // Act & Assert
        mockMvc.perform(post("/auth/refresh-token")
                        .header("Authorization", "Bearer invalid_refresh_token_string"))
                .andExpect(status().isBadRequest()); // Assuming controller or global handler maps IOException to 400

        verify(authenticationService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}