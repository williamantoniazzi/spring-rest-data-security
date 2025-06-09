package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    @Spy // Use @Spy to allow calling real methods on JwtService while still being able to mock dependencies if any
    private JwtService jwtService;

    // Use a fixed secret key for testing
    private final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long TEST_JWT_EXPIRATION = TimeUnit.HOURS.toMillis(1); // 1 hour
    private final long TEST_REFRESH_EXPIRATION = TimeUnit.DAYS.toMillis(7); // 7 days

    private User testUser;

    @BeforeEach
    void setUp() {
        // Inject the secret key and expiration times into the JwtService instance
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_JWT_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", TEST_REFRESH_EXPIRATION);

        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should extract username from a valid token")
    void extractUsername_ValidToken() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals(testUser.getUsername(), username);
    }

    @Test
    @DisplayName("Should generate a valid access token")
    void generateToken_AccessToken() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act & Assert
        assertNotNull(token);
        assertEquals(testUser.getUsername(), jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));

        // Verify expiration time (approximate)
        Claims claims = jwtService.extractAllClaims(token);
        long expectedExpirationMillis = System.currentTimeMillis() + TEST_JWT_EXPIRATION;
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
        assertTrue(claims.getExpiration().getTime() <= expectedExpirationMillis + 1000); // Allow for small time difference
    }

    @Test
    @DisplayName("Should generate a valid refresh token")
    void generateToken_RefreshToken() {
        // Arrange
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // Act & Assert
        assertNotNull(refreshToken);
        assertEquals(testUser.getUsername(), jwtService.extractUsername(refreshToken));
        assertFalse(jwtService.isTokenExpired(refreshToken));

        // Verify expiration time (approximate)
        Claims claims = jwtService.extractAllClaims(refreshToken);
        long expectedExpirationMillis = System.currentTimeMillis() + TEST_REFRESH_EXPIRATION;
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
        assertTrue(claims.getExpiration().getTime() <= expectedExpirationMillis + 1000); // Allow for small time difference
    }

    @Test
    @DisplayName("Should validate a token successfully")
    void isTokenValid_ValidToken() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act & Assert
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("Should invalidate a token with incorrect username")
    void isTokenValid_WrongUsername() {
        // Arrange
        String token = jwtService.generateToken(testUser);
        User anotherUser = User.builder()
                .email("another@example.com")
                .build();

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    @DisplayName("Should invalidate an expired token")
    void isTokenValid_ExpiredToken() throws InterruptedException {
        // Arrange
        // Temporarily set a very short expiration for testing expired token
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 millisecond
        String token = jwtService.generateToken(testUser);
        // Wait for the token to expire
        Thread.sleep(50); // Give it some time to actually expire

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, testUser));

        // Reset expiration time to original for other tests
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Should extract specific claim from token")
    void extractClaim_SpecificClaim() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");
        String token = jwtService.generateToken(extraClaims, testUser);

        // Act
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        // Assert
        assertEquals("USER", roleClaim);
    }

    @Test
    @DisplayName("Should extract all claims from token")
    void extractAllClaims_ValidToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "value");
        String token = jwtService.generateToken(extraClaims, testUser);

        // Act
        Claims claims = jwtService.extractAllClaims(token);

        // Assert
        assertNotNull(claims);
        assertEquals(testUser.getUsername(), claims.getSubject());
        assertEquals("value", claims.get("customClaim"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Should return true for an expired token")
    void isTokenExpired_True() throws InterruptedException {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 millisecond
        String token = jwtService.generateToken(testUser);
        Thread.sleep(50); // Ensure token expires

        // Act
        boolean expired = ReflectionTestUtils.invokeMethod(jwtService, "isTokenExpired", token);

        // Assert
        assertTrue(expired);

        // Reset
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Should return false for a non-expired token")
    void isTokenExpired_False() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        boolean expired = ReflectionTestUtils.invokeMethod(jwtService, "isTokenExpired", token);

        // Assert
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should return the correct signing key")
    void getSignInKey_CorrectKey() {
        // Act
        Key key = ReflectionTestUtils.invokeMethod(jwtService, "getSignInKey");

        // Assert
        assertNotNull(key);
        // Verify that the key derived from TEST_SECRET_KEY is equal to the one returned
        assertEquals(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET_KEY)), key);
    }
}
