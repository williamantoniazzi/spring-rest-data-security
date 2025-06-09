package br.edu.fatecsjc.lgnspringapi.config;

import br.edu.fatecsjc.lgnspringapi.entity.Token;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.enums.TokenType;
import br.edu.fatecsjc.lgnspringapi.repository.TokenRepository;
import br.edu.fatecsjc.lgnspringapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;
    private String validJwt;
    private Token validToken;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // Ensure clean context before each test

        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        validJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjI0MDIwMDAwLCJleHAiOjE2MjQwMjM2MDB9.SIGNATURE"; // A mock valid JWT
        validToken = Token.builder()
                .id(1L)
                .token(validJwt)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should process request with valid JWT and authenticate user")
    void doFilterInternal_ValidJwt_AuthenticatesUser() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/secure"); // A path that requires auth
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(testUser.getEmail());
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(testUser);
        when(tokenRepository.findByToken(validJwt)).thenReturn(Optional.of(validToken));
        when(jwtService.isTokenValid(validJwt, testUser)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals(testUser.getUsername(), SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response); // Ensure filter chain continues
    }

    @Test
    @DisplayName("Should not authenticate if JWT is missing")
    void doFilterInternal_NoJwt_DoesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(anyString()); // JwtService should not be called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate if JWT format is invalid")
    void doFilterInternal_InvalidJwtFormat_DoesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate if user email cannot be extracted from JWT")
    void doFilterInternal_CannotExtractUserEmail_DoesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(null); // No username extracted

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate if token is revoked or expired in repository")
    void doFilterInternal_TokenRevokedOrExpired_DoesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        validToken.setRevoked(true); // Mark token as revoked
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(testUser.getEmail());
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(testUser);
        when(tokenRepository.findByToken(validJwt)).thenReturn(Optional.of(validToken));
        when(jwtService.isTokenValid(validJwt, testUser)).thenReturn(true); // JWT itself might be valid, but repository says no

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate if JWT is invalid according to JwtService")
    void doFilterInternal_JwtServiceTokenInvalid_DoesNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(testUser.getEmail());
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(testUser);
        when(tokenRepository.findByToken(validJwt)).thenReturn(Optional.of(validToken));
        when(jwtService.isTokenValid(validJwt, testUser)).thenReturn(false); // JwtService says invalid

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should skip filter for /auth paths")
    void doFilterInternal_AuthPath_SkipsFilterLogic() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/auth/authenticate"); // Auth path

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // No authentication-related mocks should be called for /auth paths
        verify(request, never()).getHeader("Authorization");
        verify(jwtService, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(tokenRepository, never()).findByToken(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}