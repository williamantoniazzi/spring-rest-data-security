package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.MarathonRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.MarathonResponseDTO;
import br.edu.fatecsjc.lgnspringapi.exception.ResourceNotFoundException;
import br.edu.fatecsjc.lgnspringapi.service.MarathonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarathonResource.class) // Testa apenas o controlador especificado
@DisplayName("MarathonResource Tests")
class MarathonResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Cria um mock para o serviço e o injeta no controlador
    private MarathonService marathonService;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos Java em JSON

    private MarathonRequestDTO sampleRequestDTO;
    private MarathonResponseDTO sampleResponseDTO;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        sampleRequestDTO = MarathonRequestDTO.builder()
                .identification("Test Marathon Request")
                .weight(100.0)
                .score(9.0)
                .build();

        sampleResponseDTO = MarathonResponseDTO.builder()
                .id(1L)
                .identification("Test Marathon Response")
                .weight(100.0)
                .score(9.0)
                .build();
    }

    @Test
    @DisplayName("POST /api/marathons - Should create a new marathon")
    void createMarathon_ShouldReturnCreatedMarathon() throws Exception {
        when(marathonService.createMarathon(any(MarathonRequestDTO.class))).thenReturn(sampleResponseDTO);

        mockMvc.perform(post("/api/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isCreated()) // Espera status 201 Created
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.identification", is("Test Marathon Response")));

        verify(marathonService, times(1)).createMarathon(any(MarathonRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/marathons - Should return all marathons")
    void getAllMarathons_ShouldReturnListOfMarathons() throws Exception {
        List<MarathonResponseDTO> marathons = Arrays.asList(
                sampleResponseDTO,
                MarathonResponseDTO.builder().id(2L).identification("Marathon 2").weight(50.0).score(7.0).build()
        );
        when(marathonService.getAllMarathons()).thenReturn(marathons);

        mockMvc.perform(get("/api/marathons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].identification", is("Marathon 2")));

        verify(marathonService, times(1)).getAllMarathons();
    }

    @Test
    @DisplayName("GET /api/marathons/{id} - Should return marathon by ID")
    void getMarathonById_ShouldReturnMarathon() throws Exception {
        when(marathonService.getMarathonById(1L)).thenReturn(sampleResponseDTO);

        mockMvc.perform(get("/api/marathons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.identification", is("Test Marathon Response")));

        verify(marathonService, times(1)).getMarathonById(1L);
    }

    @Test
    @DisplayName("GET /api/marathons/{id} - Should return 404 if marathon not found")
    void getMarathonById_ShouldReturnNotFound() throws Exception {
        when(marathonService.getMarathonById(anyLong())).thenThrow(new ResourceNotFoundException("Marathon not found"));

        mockMvc.perform(get("/api/marathons/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera status 404 Not Found

        verify(marathonService, times(1)).getMarathonById(99L);
    }

    @Test
    @DisplayName("PUT /api/marathons/{id} - Should update an existing marathon")
    void updateMarathon_ShouldReturnUpdatedMarathon() throws Exception {
        MarathonRequestDTO updateRequest = MarathonRequestDTO.builder()
                .identification("Updated Marathon Name")
                .weight(110.0)
                .score(9.5)
                .build();
        MarathonResponseDTO updatedResponse = MarathonResponseDTO.builder()
                .id(1L)
                .identification("Updated Marathon Name")
                .weight(110.0)
                .score(9.5)
                .build();

        when(marathonService.updateMarathon(anyLong(), any(MarathonRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/marathons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.identification", is("Updated Marathon Name")));

        verify(marathonService, times(1)).updateMarathon(eq(1L), any(MarathonRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/marathons/{id} - Should return 404 if marathon not found for update")
    void updateMarathon_ShouldReturnNotFound() throws Exception {
        when(marathonService.updateMarathon(anyLong(), any(MarathonRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Marathon not found for update"));

        mockMvc.perform(put("/api/marathons/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isNotFound()); // Espera status 404 Not Found

        verify(marathonService, times(1)).updateMarathon(eq(99L), any(MarathonRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/marathons/{id} - Should delete a marathon")
    void deleteMarathon_ShouldReturnNoContent() throws Exception {
        doNothing().when(marathonService).deleteMarathon(1L);

        mockMvc.perform(delete("/api/marathons/{id}", 1L))
                .andExpect(status().isNoContent()); // Espera status 204 No Content

        verify(marathonService, times(1)).deleteMarathon(1L);
    }

    @Test
    @DisplayName("DELETE /api/marathons/{id} - Should return 404 if marathon not found for deletion")
    void deleteMarathon_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Marathon not found for deletion"))
                .when(marathonService).deleteMarathon(anyLong());

        mockMvc.perform(delete("/api/marathons/{id}", 99L))
                .andExpect(status().isNotFound()); // Espera status 404 Not Found

        verify(marathonService, times(1)).deleteMarathon(99L);
    }

    @Test
    @DisplayName("POST /api/marathons - Should return 400 for invalid request body (blank identification)")
    void createMarathon_InvalidIdentification_ShouldReturnBadRequest() throws Exception {
        MarathonRequestDTO invalidRequest = MarathonRequestDTO.builder()
                .identification("") // Blank identification
                .weight(100.0)
                .score(9.0)
                .build();

        mockMvc.perform(post("/api/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // Espera status 400 Bad Request
                .andExpect(jsonPath("$.identification", is("A identificação da maratona é obrigatória."))); // Verifica a mensagem de erro
    }

    @Test
    @DisplayName("POST /api/marathons - Should return 400 for invalid request body (null weight)")
    void createMarathon_NullWeight_ShouldReturnBadRequest() throws Exception {
        MarathonRequestDTO invalidRequest = MarathonRequestDTO.builder()
                .identification("Valid ID")
                .weight(null) // Null weight
                .score(9.0)
                .build();

        mockMvc.perform(post("/api/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.weight", is("O peso da maratona é obrigatório.")));
    }
}