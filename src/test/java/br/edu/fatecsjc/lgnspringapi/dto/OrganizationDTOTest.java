package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Address;
import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationDTOTest {

    @Test
    @DisplayName("OrganizationRequestDTO should correctly convert to Organization entity")
    void toEntity_Success() {
        AddressDTO addressDTO = AddressDTO.builder()
                .street("Rua Exemplo")
                .number("123")
                .neighborhood("Centro")
                .zipCode("12345-678")
                .city("São José dos Campos")
                .state("SP")
                .build();

        OrganizationRequestDTO requestDTO = OrganizationRequestDTO.builder()
                .name("Nova Empresa")
                .address(addressDTO)
                .institutionName("Instituição XYZ")
                .headquartersCountry("Brasil")
                .build();

        Organization organization = requestDTO.toEntity();

        assertThat(organization).isNotNull();
        assertThat(organization.getName()).isEqualTo("Nova Empresa");
        assertThat(organization.getInstitutionName()).isEqualTo("Instituição XYZ");
        assertThat(organization.getHeadquartersCountry()).isEqualTo("Brasil");

        assertThat(organization.getAddress()).isNotNull();
        assertThat(organization.getAddress().getStreet()).isEqualTo("Rua Exemplo");
        assertThat(organization.getAddress().getNumber()).isEqualTo("123");
        assertThat(organization.getAddress().getNeighborhood()).isEqualTo("Centro");
        assertThat(organization.getAddress().getZipCode()).isEqualTo("12345-678");
        assertThat(organization.getAddress().getCity()).isEqualTo("São José dos Campos");
        assertThat(organization.getAddress().getState()).isEqualTo("SP");
    }

    @Test
    @DisplayName("OrganizationRequestDTO should convert to entity with null address if addressDTO is null")
    void toEntity_NullAddressDTO() {
        OrganizationRequestDTO requestDTO = OrganizationRequestDTO.builder()
                .name("Empresa Sem Endereço")
                .address(null)
                .institutionName("Instituição ABC")
                .headquartersCountry("EUA")
                .build();

        Organization organization = requestDTO.toEntity();

        assertThat(organization).isNotNull();
        assertThat(organization.getName()).isEqualTo("Empresa Sem Endereço");
        assertThat(organization.getAddress()).isNull();
    }

    @Test
    @DisplayName("OrganizationResponseDTO should correctly convert from Organization entity with groups")
    void fromEntity_WithGroups() {
        Address address = Address.builder()
                .street("Av. Principal")
                .city("Cidade Teste")
                .build();

        Organization organization = Organization.builder()
                .id(1L)
                .name("Org Teste")
                .address(address)
                .institutionName("Inst Teste")
                .headquartersCountry("País Teste")
                .build();

        Group group1 = Group.builder().id(10L).name("Grupo A").organization(organization).build();
        Group group2 = Group.builder().id(11L).name("Grupo B").organization(organization).build();
        organization.setGroups(Arrays.asList(group1, group2)); // Set groups in the entity

        OrganizationResponseDTO responseDTO = OrganizationResponseDTO.fromEntity(organization);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getName()).isEqualTo("Org Teste");
        assertThat(responseDTO.getInstitutionName()).isEqualTo("Inst Teste");
        assertThat(responseDTO.getHeadquartersCountry()).isEqualTo("País Teste");

        assertThat(responseDTO.getAddress()).isNotNull();
        assertThat(responseDTO.getAddress().getStreet()).isEqualTo("Av. Principal");
        assertThat(responseDTO.getAddress().getCity()).isEqualTo("Cidade Teste");

        assertThat(responseDTO.getGroups()).isNotNull().hasSize(2);
        assertThat(responseDTO.getGroups().get(0).getId()).isEqualTo(10L);
        assertThat(responseDTO.getGroups().get(0).getName()).isEqualTo("Grupo A");
        assertThat(responseDTO.getGroups().get(1).getId()).isEqualTo(11L);
        assertThat(responseDTO.getGroups().get(1).getName()).isEqualTo("Grupo B");
    }

    @Test
    @DisplayName("OrganizationResponseDTO should correctly convert from Organization entity without groups")
    void fromEntity_WithoutGroups() {
        Address address = Address.builder()
                .street("Rua Sem Grupos")
                .city("Cidade Sem Grupos")
                .build();

        Organization organization = Organization.builder()
                .id(2L)
                .name("Org Sem Grupos")
                .address(address)
                .institutionName("Inst Sem Grupos")
                .headquartersCountry("Outro País")
                .groups(Collections.emptyList()) // Explicitly set empty list
                .build();

        OrganizationResponseDTO responseDTO = OrganizationResponseDTO.fromEntity(organization);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(2L);
        assertThat(responseDTO.getName()).isEqualTo("Org Sem Grupos");
        assertThat(responseDTO.getGroups()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("OrganizationResponseDTO should return null when converting from null entity")
    void fromEntity_NullEntity() {
        OrganizationResponseDTO responseDTO = OrganizationResponseDTO.fromEntity(null);
        assertThat(responseDTO).isNull();
    }

    @Test
    @DisplayName("OrganizationResponseDTO should handle null address in entity")
    void fromEntity_NullAddressInEntity() {
        Organization organization = Organization.builder()
                .id(3L)
                .name("Org Null Address")
                .address(null) // Address is null
                .institutionName("Inst Null Address")
                .headquartersCountry("País X")
                .build();

        OrganizationResponseDTO responseDTO = OrganizationResponseDTO.fromEntity(organization);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(3L);
        assertThat(responseDTO.getName()).isEqualTo("Org Null Address");
        assertThat(responseDTO.getAddress()).isNull();
    }
}