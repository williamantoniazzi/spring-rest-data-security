package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import br.edu.fatecsjc.lgnspringapi.service.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationResource.class)
class OrganizationResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(1L)
                .name("Fatec São José dos Campos")
                .institutionName("Fatec")
                .headquartersCountry("Brazil")
                .build();
    }

    @Test
    @DisplayName("Should create an organization and return 201 Created")
    void createOrganization_Success() throws Exception {
        when(organizationService.createOrganization(any(Organization.class))).thenReturn(organization);

        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fatec São José dos Campos"));

        verify(organizationService, times(1)).createOrganization(any(Organization.class));
    }

    @Test
    @DisplayName("Should get an organization by ID and return 200 OK")
    void getOrganizationById_Exists() throws Exception {
        when(organizationService.getOrganizationById(1L)).thenReturn(Optional.of(organization));

        mockMvc.perform(get("/api/organizations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fatec São José dos Campos"));

        verify(organizationService, times(1)).getOrganizationById(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when organization by ID does not exist")
    void getOrganizationById_NotFound() throws Exception {
        when(organizationService.getOrganizationById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/organizations/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(organizationService, times(1)).getOrganizationById(2L);
    }

    @Test
    @DisplayName("Should get all organizations and return 200 OK")
    void getAllOrganizations_Success() throws Exception {
        Organization organization2 = Organization.builder().id(2L).name("USP").build();
        List<Organization> organizationsList = Arrays.asList(organization, organization2);

        when(organizationService.getAllOrganizations()).thenReturn(organizationsList);

        mockMvc.perform(get("/api/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Fatec São José dos Campos"))
                .andExpect(jsonPath("$[1].name").value("USP"));

        verify(organizationService, times(1)).getAllOrganizations();
    }

    @Test
    @DisplayName("Should update an organization and return 200 OK")
    void updateOrganization_Success() throws Exception {
        Organization updatedInfo = Organization.builder()
                .id(1L)
                .name("Fatec São José dos Campos (Updated)")
                .institutionName("Fatec Updated")
                .headquartersCountry("Brazil Updated")
                .build();

        when(organizationService.updateOrganization(eq(1L), any(Organization.class))).thenReturn(updatedInfo);

        mockMvc.perform(put("/api/organizations/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fatec São José dos Campos (Updated)"));

        verify(organizationService, times(1)).updateOrganization(eq(1L), any(Organization.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a non-existent organization")
    void updateOrganization_NotFound() throws Exception {
        when(organizationService.updateOrganization(eq(2L), any(Organization.class)))
                .thenThrow(new EntityNotFoundException("Organization not found"));

        mockMvc.perform(put("/api/organizations/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isNotFound());

        verify(organizationService, times(1)).updateOrganization(eq(2L), any(Organization.class));
    }

    @Test
    @DisplayName("Should delete an organization and return 204 No Content")
    void deleteOrganization_Success() throws Exception {
        doNothing().when(organizationService).deleteOrganization(1L);

        mockMvc.perform(delete("/api/organizations/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(organizationService, times(1)).deleteOrganization(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a non-existent organization")
    void deleteOrganization_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Organization not found")).when(organizationService).deleteOrganization(2L);

        mockMvc.perform(delete("/api/organizations/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(organizationService, times(1)).deleteOrganization(2L);
    }
}