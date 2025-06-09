package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.AuthenticationResponseDTO;
import br.edu.fatecsjc.lgnspringapi.dto.RegisterRequestDTO;
import br.edu.fatecsjc.lgnspringapi.entity.Token;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.enums.TokenType;
import br.edu.fatecsjc.lgnspringapi.repository.TokenRepository;
import br.edu.fatecsjc.lgnspringapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequestDTO registerRequestDTO;
    private AuthenticationRequestDTO authenticationRequestDTO;
    private User testUser;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() throws IOException {
        registerRequestDTO = RegisterRequestDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword123")
                .role(Role.USER)
                .build();

        accessToken = "mock_access_token";
        refreshToken = "mock_refresh_token";

        when(response.getOutputStream()).thenReturn(mock(jakarta.servlet.ServletOutputStream.class));
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void register_Success() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        doNothing().when(tokenRepository).save(any(Token.class));

        // Act
        AuthenticationResponseDTO responseDTO = authenticationService.register(registerRequestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(accessToken, responseDTO.getAccessToken());
        assertEquals(refreshToken, responseDTO.getRefreshToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("John", savedUser.getFirstName());
        assertEquals("encodedPassword123", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    @DisplayName("Should authenticate user successfully and generate tokens")
    void authenticate_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        doNothing().when(tokenRepository).saveAll(anyList());
        doNothing().when(tokenRepository).save(any(Token.class));

        // Act
        AuthenticationResponseDTO responseDTO = authenticationService.authenticate(authenticationRequestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(accessToken, responseDTO.getAccessToken());
        assertEquals(refreshToken, responseDTO.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(tokenRepository, times(1)).findAllValidTokenByUser(testUser.getId());
        verify(tokenRepository, times(1)).saveAll(anyList());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid authentication credentials")
    void authenticate_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticate(authenticationRequestDTO)
        );

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_Success() throws IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(refreshToken, testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("new_access_token");
        when(tokenRepository.findAllValidTokenByUser(testUser.getId())).thenReturn(Collections.emptyList()); // No existing valid tokens
        doNothing().when(tokenRepository).save(any(Token.class));
        when(response.getOutputStream()).thenReturn(mock(jakarta.servlet.ServletOutputStream.class));

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, times(1)).extractUsername(refreshToken);
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(jwtService, times(1)).isTokenValid(refreshToken, testUser);
        verify(jwtService, times(1)).generateToken(testUser);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(testUser.getId());
        verify(tokenRepository, times(1)).save(any(Token.class)); // Verifies saveUserToken
        verify(response, times(1)).getOutputStream(); // Verifies response is written to
    }

    @Test
    @DisplayName("Should not refresh token if Authorization header is missing")
    void refreshToken_NoAuthHeader() throws IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, never()).extractUsername(anyString());
        verify(response, never()).getOutputStream();
    }

    @Test
    @DisplayName("Should not refresh token if token is invalid")
    void refreshToken_InvalidToken() throws IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(refreshToken, testUser)).thenReturn(false); // Invalid token

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, times(1)).isTokenValid(refreshToken, testUser);
        verify(jwtService, never()).generateToken(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
        verify(response, never()).getOutputStream();
    }

    @Test
    @DisplayName("Should revoke all user tokens before saving new token during refresh")
    void refreshToken_RevokeExistingTokens() throws IOException {
        // Arrange
        Token existingValidToken1 = Token.builder()
                .id(1L).token("validToken1").tokenType(TokenType.BEARER).expired(false).revoked(false).user(testUser).build();
        Token existingValidToken2 = Token.builder()
                .id(2L).token("validToken2").tokenType(TokenType.BEARER).expired(false).revoked(false).user(testUser).build();
        List<Token> validTokens = List.of(existingValidToken1, existingValidToken2);


        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(refreshToken, testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("new_access_token");
        when(tokenRepository.findAllValidTokenByUser(testUser.getId())).thenReturn(validTokens);
        doNothing().when(tokenRepository).saveAll(anyList());
        doNothing().when(tokenRepository).save(any(Token.class));
        when(response.getOutputStream()).thenReturn(mock(jakarta.servlet.ServletOutputStream.class));

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        ArgumentCaptor<List> revokedTokensCaptor = ArgumentCaptor.forClass(List.class);
        verify(tokenRepository, times(1)).saveAll(revokedTokensCaptor.capture());
        List<Token> capturedTokens = revokedTokensCaptor.getValue();
        assertEquals(2, capturedTokens.size());
        assertTrue(capturedTokens.stream().allMatch(Token::isExpired));
        assertTrue(capturedTokens.stream().allMatch(Token::isRevoked));
    }
}