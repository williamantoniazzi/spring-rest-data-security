package br.edu.fatecsjc.lgnspringapi.entity;

import br.edu.fatecsjc.lgnspringapi.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .password("securePassword123")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should return correct authorities for USER role")
    void getAuthorities_UserRole() {
        // Arrange
        user.setRole(Role.USER);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size()); // USER role only has ROLE_USER authority
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Should return correct authorities for ADMIN role including permissions")
    void getAuthorities_AdminRole() {
        // Arrange
        user.setRole(Role.ADMIN);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(3, authorities.size()); // ADMIN_CREATE, ADMIN_UPDATE, and ROLE_ADMIN
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:create")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:update")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should return email as username")
    void getUsername() {
        // Act
        String username = user.getUsername();

        // Assert
        assertEquals("jane.doe@example.com", username);
    }

    @Test
    @DisplayName("Should return password")
    void getPassword() {
        // Act
        String password = user.getPassword();

        // Assert
        assertEquals("securePassword123", password);
    }

    @Test
    @DisplayName("isAccountNonExpired should always return true")
    void isAccountNonExpired() {
        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    @DisplayName("isAccountNonLocked should always return true")
    void isAccountNonLocked() {
        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("isCredentialsNonExpired should always return true")
    void isCredentialsNonExpired() {
        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("isEnabled should always return true")
    void isEnabled() {
        // Act & Assert
        assertTrue(user.isEnabled());
    }
}