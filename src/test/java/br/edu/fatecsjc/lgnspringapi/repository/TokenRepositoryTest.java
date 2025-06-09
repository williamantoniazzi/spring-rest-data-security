package br.edu.fatecsjc.lgnspringapi.repository;

import br.edu.fatecsjc.lgnspringapi.entity.Token;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TestEntityManager entityManager; // Used to persist entities directly for testing

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        entityManager.persistAndFlush(testUser); // Persist user first

        anotherUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        entityManager.persistAndFlush(anotherUser); // Persist another user
    }

    @Test
    @DisplayName("Should find all valid tokens by user ID")
    void findAllValidTokenByUser_Success() {
        // Arrange
        Token validToken1 = Token.builder()
                .token("jwt1")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(testUser)
                .build();
        entityManager.persist(validToken1);

        Token validToken2 = Token.builder()
                .token("jwt2")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(testUser)
                .build();
        entityManager.persist(validToken2);

        Token expiredToken = Token.builder()
                .token("jwt3")
                .tokenType(TokenType.BEARER)
                .expired(true) // Expired
                .revoked(false)
                .user(testUser)
                .build();
        entityManager.persist(expiredToken);

        Token revokedToken = Token.builder()
                .token("jwt4")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(true) // Revoked
                .user(testUser)
                .build();
        entityManager.persist(revokedToken);

        Token anotherUserToken = Token.builder()
                .token("jwt5")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(anotherUser) // Belongs to another user
                .build();
        entityManager.persist(anotherUserToken);

        entityManager.flush(); // Ensure all changes are flushed to the database

        // Act
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(testUser.getId());

        // Assert
        assertNotNull(validTokens);
        assertEquals(2, validTokens.size());
        assertTrue(validTokens.stream().anyMatch(t -> t.getToken().equals("jwt1")));
        assertTrue(validTokens.stream().anyMatch(t -> t.getToken().equals("jwt2")));
        assertFalse(validTokens.stream().anyMatch(t -> t.getToken().equals("jwt3")));
        assertFalse(validTokens.stream().anyMatch(t -> t.getToken().equals("jwt4")));
        assertFalse(validTokens.stream().anyMatch(t -> t.getToken().equals("jwt5")));
    }

    @Test
    @DisplayName("Should return empty list if no valid tokens found for user ID")
    void findAllValidTokenByUser_NoValidTokens() {
        // Arrange
        Token expiredToken = Token.builder()
                .token("jwtExpired")
                .tokenType(TokenType.BEARER)
                .expired(true)
                .revoked(false)
                .user(testUser)
                .build();
        entityManager.persist(expiredToken);

        Token revokedToken = Token.builder()
                .token("jwtRevoked")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(true)
                .user(testUser)
                .build();
        entityManager.persist(revokedToken);
        entityManager.flush();

        // Act
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(testUser.getId());

        // Assert
        assertNotNull(validTokens);
        assertTrue(validTokens.isEmpty());
    }

    @Test
    @DisplayName("Should find token by token string successfully")
    void findByToken_Success() {
        // Arrange
        Token token = Token.builder()
                .token("uniqueJwtString")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(token);

        // Act
        Optional<Token> foundToken = tokenRepository.findByToken("uniqueJwtString");

        // Assert
        assertTrue(foundToken.isPresent());
        assertEquals(token.getToken(), foundToken.get().getToken());
    }

    @Test
    @DisplayName("Should return empty optional if token string not found")
    void findByToken_NotFound() {
        // Arrange (no token with this string is persisted)

        // Act
        Optional<Token> foundToken = tokenRepository.findByToken("nonexistentJwtString");

        // Assert
        assertFalse(foundToken.isPresent());
    }
}