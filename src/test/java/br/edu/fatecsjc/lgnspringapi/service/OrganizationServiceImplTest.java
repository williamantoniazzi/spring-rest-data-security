package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import br.edu.fatecsjc.lgnspringapi.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

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
    @DisplayName("Should create an organization successfully")
    void createOrganization_Success() {
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

        Organization createdOrganization = organizationService.createOrganization(organization);

        assertThat(createdOrganization).isNotNull();
        assertThat(createdOrganization.getName()).isEqualTo("Fatec São José dos Campos");
        verify(organizationRepository, times(1)).save(organization);
    }

    @Test
    @DisplayName("Should retrieve an organization by ID when it exists")
    void getOrganizationById_Exists() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        Optional<Organization> foundOrganization = organizationService.getOrganizationById(1L);

        assertThat(foundOrganization).isPresent();
        assertThat(foundOrganization.get().getName()).isEqualTo("Fatec São José dos Campos");
        verify(organizationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when organization by ID does not exist")
    void getOrganizationById_NotFound() {
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Organization> foundOrganization = organizationService.getOrganizationById(2L);

        assertThat(foundOrganization).isNotPresent();
        verify(organizationRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Should retrieve all organizations")
    void getAllOrganizations_Success() {
        Organization organization2 = Organization.builder().id(2L).name("USP").build();
        List<Organization> organizations = Arrays.asList(organization, organization2);

        when(organizationRepository.findAll()).thenReturn(organizations);

        List<Organization> foundOrganizations = organizationService.getAllOrganizations();

        assertThat(foundOrganizations).isNotNull().hasSize(2);
        assertThat(foundOrganizations).contains(organization, organization2);
        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update an existing organization successfully")
    void updateOrganization_Success() {
        Organization updatedInfo = Organization.builder()
                .name("Fatec São José dos Campos (Updated)")
                .institutionName("Fatec Updated")
                .headquartersCountry("Brazil Updated")
                .build();

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(updatedInfo);

        Organization updatedOrganization = organizationService.updateOrganization(1L, updatedInfo);

        assertThat(updatedOrganization).isNotNull();
        assertThat(updatedOrganization.getName()).isEqualTo("Fatec São José dos Campos (Updated)");
        assertThat(updatedOrganization.getInstitutionName()).isEqualTo("Fatec Updated");
        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).save(any(Organization.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent organization")
    void updateOrganization_NotFound() {
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> organizationService.updateOrganization(2L, organization));
        verify(organizationRepository, times(1)).findById(2L);
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("Should delete an existing organization successfully")
    void deleteOrganization_Success() {
        when(organizationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(organizationRepository).deleteById(1L);

        organizationService.deleteOrganization(1L);

        verify(organizationRepository, times(1)).existsById(1L);
        verify(organizationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting a non-existent organization")
    void deleteOrganization_NotFound() {
        when(organizationRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> organizationService.deleteOrganization(2L));
        verify(organizationRepository, times(1)).existsById(2L);
        verify(organizationRepository, never()).deleteById(anyLong());
    }
}