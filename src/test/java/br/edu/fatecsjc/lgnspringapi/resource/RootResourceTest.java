package br.edu.fatecsjc.lgnspringapi.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.util.ReflectionTestUtils; // For injecting @Value

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootResource.class)
class RootResourceTest {

    @Autowired
    private MockMvc mockMvc;

    // We need to inject the @Value for the port, as @WebMvcTest doesn't fully load the Spring application context
    // that would normally resolve @Value annotations.
    private final String MOCK_SERVER_PORT = "8000";

    @BeforeEach
    void setUp() {
        // Inject the mock port value into the RootResource instance
        // This is necessary because @Value is not resolved in a pure @WebMvcTest slice
        // without a full Spring context.
        ReflectionTestUtils.setField(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(RootResource.class), "port", MOCK_SERVER_PORT);
    }

    @Test
    @DisplayName("Should return welcome message with Swagger UI link")
    void validateRestResource_ReturnsWelcomeMessage() throws Exception {
        // Arrange
        String expectedMessage = new StringBuilder()
                .append("Bem-vindo, APIs operacionais. ")
                .append(String.format("Acesse: <a href=\"http://localhost:%s/swagger-ui/index.html\">", MOCK_SERVER_PORT))
                .append(String.format("http://localhost:%s/swagger-ui/index.html</a>", MOCK_SERVER_PORT))
                .toString();

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }
}