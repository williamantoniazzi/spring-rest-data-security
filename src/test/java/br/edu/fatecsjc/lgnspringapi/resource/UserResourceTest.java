package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.ChangePasswordRequestDTO;
import br.edu.fatecsjc.lgnspringapi.entity.User;
import br.edu.fatecsjc.lgnspringapi.enums.Role;
import br.edu.fatecsjc.lgnspringapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserResource.class)
class UserResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private ChangePasswordRequestDTO changePasswordRequestDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .currentPassword("old_password")
                .newPassword("new_password")
                .confirmationPassword("new_password")
                .build();

        mockUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encoded_old_password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should return 200 OK when password change is successful")
    @WithMockUser(username = "test@example.com", roles = "USER") // Simulate a logged-in user
    void changePassword_Success() throws Exception {
        // Arrange
        doNothing().when(userService).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));

        // Act & Assert
        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO)))
                .andExpect(status().isOk());

        verify(userService, times(1)).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when current password is incorrect")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void changePassword_WrongPassword() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("Wrong password"))
                .when(userService).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));

        // Act & Assert
        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO)))
                .andExpect(status().isBadRequest()); // Assuming a global exception handler would map IllegalStateException to 400

        verify(userService, times(1)).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when new password and confirmation do not match")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void changePassword_PasswordsDoNotMatch() throws Exception {
        // Arrange
        changePasswordRequestDTO.setConfirmationPassword("mismatch_password"); // Modify request for mismatch
        doThrow(new IllegalStateException("Password are not the same"))
                .when(userService).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));

        // Act & Assert
        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO)))
                .andExpect(status().isBadRequest()); // Assuming a global exception handler would map IllegalStateException to 400

        verify(userService, times(1)).changePassword(any(ChangePasswordRequestDTO.class), any(Principal.class));
    }

    // Helper to simulate Principal for @WithMockUser not always being enough
    // This part is often handled by Spring Security's TestContext or custom Mockito setups
    // For simplicity with @WebMvcTest, it's often better to rely on @WithMockUser and mock service.
    // If you were to fully mock Principal, it would look like this:
    private Principal getMockPrincipal(UserDetails userDetails) {
        return (Principal) () -> userDetails.getUsername(); // A basic Principal implementation for testing
    }
}