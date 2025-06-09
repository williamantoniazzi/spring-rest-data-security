package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.entity.Token;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.enums.TokenType;
import br.edu.fatecsjc.lgnspringapi.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    private String testJwt;
    private Token testToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // Clear context before each test

        testJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."; // Mock JWT
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .email("john.doe@example.com")
                .role(Role.USER)
                .build();
        testToken = Token.builder()
                .id(1L)
                .token(testJwt)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should invalidate token and clear security context on successful logout")
    void logout_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testJwt);
        when(tokenRepository.findByToken(testJwt)).thenReturn(Optional.of(testToken));

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).findByToken(testJwt);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        Token capturedToken = tokenCaptor.getValue();
        assertTrue(capturedToken.isExpired());
        assertTrue(capturedToken.isRevoked());

        // Verify SecurityContextHolder is cleared
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should do nothing if Authorization header is missing")
    void logout_NoAuthHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
        // Security context should still be null if it was null initially, or unchanged if it had something
        assertNull(SecurityContextHolder.getContext().getAuthentication()); // Assuming it starts null for unit test
    }

    @Test
    @DisplayName("Should do nothing if Authorization header does not start with Bearer")
    void logout_InvalidAuthHeaderFormat() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic abcdef");

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    @DisplayName("Should do nothing if token is not found in repository")
    void logout_TokenNotFound() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testJwt);
        when(tokenRepository.findByToken(testJwt)).thenReturn(Optional.empty()); // Token not found

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).findByToken(testJwt);
        verify(tokenRepository, never()).save(any(Token.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication()); // Context should still be null
    }
}