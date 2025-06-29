package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationResponseDTO;
import br.edu.fatecsjc.lgnspringapi.dto.RegisterRequestDTO;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationResourceTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationResource authenticationResource;

    private RegisterRequestDTO registerRequestDTO;
    private AuthenticationRequestDTO authenticationRequestDTO;
    private AuthenticationResponseDTO authenticationResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationResource).build();

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
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenReturn(authenticationResponseDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token", is("mockAccessToken")))
                .andExpect(jsonPath("$.refresh_token", is("mockRefreshToken")));

        verify(authenticationService, times(1)).register(any(RegisterRequestDTO.class));
    }


    @Test
    @DisplayName("Should return 200 OK and tokens on successful authentication")
    void authenticate_Success() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDTO.class))).thenReturn(authenticationResponseDTO);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is("mockAccessToken")))
                .andExpect(jsonPath("$.refresh_token", is("mockRefreshToken")));

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequestDTO.class));
    }


    @Test
    @DisplayName("Should return 200 OK on successful token refresh")
    void refreshToken_Success() throws Exception {
        doNothing().when(authenticationService).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        mockMvc.perform(post("/auth/refresh-token")
                        .header("Authorization", "Bearer refresh_token_string"))
                .andExpect(status().isOk());

        verify(authenticationService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }


}
