package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GroupRequestDTO Tests")
class GroupRequestDTOTest {

    // Helper para criar uma Organization simples
    private Organization createSampleOrganization(Long id, String name) {
        return Organization.builder().id(id).name(name).build();
    }

    private GroupRequestDTO createSampleGroupRequestDTO() {
        return GroupRequestDTO.builder()
                .name("Requested Group")
                .organizationId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create GroupRequestDTO using builder and verify getters")
    void shouldCreateGroupRequestDTOAndVerifyGetters() {
        GroupRequestDTO dto = createSampleGroupRequestDTO();

        assertNotNull(dto);
        assertEquals("Requested Group", dto.getName());
        assertEquals(1L, dto.getOrganizationId());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        GroupRequestDTO dto = new GroupRequestDTO();
        dto.setName("Updated Request Group");
        dto.setOrganizationId(2L);

        assertEquals("Updated Request Group", dto.getName());
        assertEquals(2L, dto.getOrganizationId());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        GroupRequestDTO dto = new GroupRequestDTO("All Args Request Group", 3L);

        assertEquals("All Args Request Group", dto.getName());
        assertEquals(3L, dto.getOrganizationId());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        GroupRequestDTO dto = createSampleGroupRequestDTO();
        String expectedToStringPart = "GroupRequestDTO(name=Requested Group, organizationId=1)";
        assertTrue(dto.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal GroupRequestDTO objects with equals and hashCode")
    void shouldCompareEqualGroupRequestDTOs() {
        GroupRequestDTO dto1 = createSampleGroupRequestDTO();
        GroupRequestDTO dto2 = createSampleGroupRequestDTO();
        GroupRequestDTO dto3 = GroupRequestDTO.builder().name("Different").organizationId(99L).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Should convert GroupRequestDTO to Group entity successfully")
    void shouldConvertToEntity() {
        GroupRequestDTO dto = createSampleGroupRequestDTO();
        Organization organization = createSampleOrganization(1L, "My Organization");

        Group group = dto.toEntity(organization);

        assertNotNull(group);
        assertNull(group.getId()); // ID deve ser nulo na criação
        assertEquals("Requested Group", group.getName());
        assertNotNull(group.getOrganization());
        assertEquals(1L, group.getOrganization().getId());
        assertEquals("My Organization", group.getOrganization().getName());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when converting to entity with null organization")
    void shouldThrowExceptionWhenConvertToEntityWithNullOrganization() {
        GroupRequestDTO dto = createSampleGroupRequestDTO();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dto.toEntity(null);
        });

        assertEquals("A organização não pode ser nula ao converter GroupRequestDTO para entidade.", exception.getMessage());
    }
}