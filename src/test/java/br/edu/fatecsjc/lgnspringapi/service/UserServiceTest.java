package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.dto.ChangePasswordRequestDTO;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Principal connectedUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_old_password")
                .role(Role.USER)
                .build();

        connectedUser = new UsernamePasswordAuthenticationToken(testUser, null);
    }

    @Test
    @DisplayName("Should change password successfully when current password is correct and new passwords match")
    void changePassword_Success() {
        // Arrange
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .currentPassword("old_password")
                .newPassword("new_password")
                .confirmationPassword("new_password")
                .build();

        when(passwordEncoder.matches("old_password", "encoded_old_password")).thenReturn(true);
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        // Act
        userService.changePassword(request, connectedUser);

        // Assert
        verify(passwordEncoder, times(1)).matches("old_password", "encoded_old_password");
        verify(passwordEncoder, times(1)).encode("new_password");
        verify(userRepository, times(1)).save(testUser);
        assertEquals("encoded_new_password", testUser.getPassword());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when current password is incorrect")
    void changePassword_WrongPassword() {
        // Arrange
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .currentPassword("wrong_password")
                .newPassword("new_password")
                .confirmationPassword("new_password")
                .build();

        when(passwordEncoder.matches("wrong_password", "encoded_old_password")).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.changePassword(request, connectedUser)
        );

        assertEquals("Wrong password", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrong_password", "encoded_old_password");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when new password and confirmation password do not match")
    void changePassword_PasswordsDoNotMatch() {
        // Arrange
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .currentPassword("old_password")
                .newPassword("new_password")
                .confirmationPassword("different_password")
                .build();

        when(passwordEncoder.matches("old_password", "encoded_old_password")).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.changePassword(request, connectedUser)
        );

        assertEquals("Password are not the same", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("old_password", "encoded_old_password");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}