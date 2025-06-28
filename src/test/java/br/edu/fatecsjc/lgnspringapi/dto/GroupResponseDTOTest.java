package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Member;
import br.edu.fatecsjc.lgnspringapi.dto.MemberResponseDTO;
import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GroupResponseDTO Tests")
class GroupResponseDTOTest {

    // Helper para criar uma Organization simples
    private Organization createSampleOrganization(Long id, String name) {
        return Organization.builder().id(id).name(name).build();
    }

    // Helper para criar um Member simples
    private Member createSampleMember(Long id, String name, String email) {
        return Member.builder().id(id).name(name).email(email).build();
    }

    // Helper para criar um MemberResponseDTO simples (se não tiver o arquivo ainda)
    private MemberResponseDTO createSampleMemberResponseDTO(Long id, String name) {
        return MemberResponseDTO.builder().id(id).name(name).build();
    }

    private GroupResponseDTO createSampleGroupResponseDTO() {
        return GroupResponseDTO.builder()
                .id(1L)
                .name("Response Group")
                .organizationId(10L)
                .organizationName("Org Response")
                .members(List.of(createSampleMemberResponseDTO(101L, "Resp Member 1")))
                .build();
    }

    @Test
    @DisplayName("Should create GroupResponseDTO using builder and verify getters")
    void shouldCreateGroupResponseDTOAndVerifyGetters() {
        GroupResponseDTO dto = createSampleGroupResponseDTO();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Response Group", dto.getName());
        assertEquals(10L, dto.getOrganizationId());
        assertEquals("Org Response", dto.getOrganizationName());
        assertNotNull(dto.getMembers());
        assertEquals(1, dto.getMembers().size());
        assertEquals("Resp Member 1", dto.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        GroupResponseDTO dto = new GroupResponseDTO();
        dto.setId(2L);
        dto.setName("Updated Response Group");
        dto.setOrganizationId(20L);
        dto.setOrganizationName("Updated Org");
        dto.setMembers(List.of(createSampleMemberResponseDTO(201L, "Resp Member 2")));

        assertEquals(2L, dto.getId());
        assertEquals("Updated Response Group", dto.getName());
        assertEquals(20L, dto.getOrganizationId());
        assertEquals("Updated Org", dto.getOrganizationName());
        assertNotNull(dto.getMembers());
        assertEquals(1, dto.getMembers().size());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        List<MemberResponseDTO> members = List.of(createSampleMemberResponseDTO(301L, "Resp Member 3"));
        GroupResponseDTO dto = new GroupResponseDTO(3L, "All Args Response Group", 30L, "All Args Org", members);

        assertEquals(3L, dto.getId());
        assertEquals("All Args Response Group", dto.getName());
        assertEquals(30L, dto.getOrganizationId());
        assertEquals("All Args Org", dto.getOrganizationName());
        assertNotNull(dto.getMembers());
        assertEquals(1, dto.getMembers().size());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        GroupResponseDTO dto = createSampleGroupResponseDTO();
        String expectedToStringPart = "GroupResponseDTO(id=1, name=Response Group, organizationId=10, organizationName=Org Response, members=[MemberResponseDTO(id=101, name=Resp Member 1)";
        assertTrue(dto.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal GroupResponseDTO objects with equals and hashCode")
    void shouldCompareEqualGroupResponseDTOs() {
        GroupResponseDTO dto1 = createSampleGroupResponseDTO();
        GroupResponseDTO dto2 = createSampleGroupResponseDTO();
        GroupResponseDTO dto3 = GroupResponseDTO.builder().id(99L).name("Different").build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("fromEntity should convert Group to GroupResponseDTO correctly")
    void fromEntityShouldConvertGroupToDto() {
        Organization organization = createSampleOrganization(10L, "Test Org");
        Member member1 = createSampleMember(101L, "Member A", "a@example.com");
        Member member2 = createSampleMember(102L, "Member B", "b@example.com");

        Group group = Group.builder()
                .id(1L)
                .name("Entity Group")
                .organization(organization)
                .members(List.of(member1, member2))
                .build();

        GroupResponseDTO dto = GroupResponseDTO.fromEntity(group);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Entity Group", dto.getName());
        assertEquals(10L, dto.getOrganizationId());
        assertEquals("Test Org", dto.getOrganizationName());
        assertNotNull(dto.getMembers());
        assertEquals(2, dto.getMembers().size());
        assertEquals("Member A", dto.getMembers().get(0).getName());
        assertEquals("Member B", dto.getMembers().get(1).getName());
    }

    @Test
    @DisplayName("fromEntity should handle null Group input")
    void fromEntityShouldHandleNullGroup() {
        assertNull(GroupResponseDTO.fromEntity(null));
    }

    @Test
    @DisplayName("fromEntity should handle Group with null Organization")
    void fromEntityShouldHandleGroupWithNullOrganization() {
        Group group = Group.builder().id(1L).name("No Org Group").members(Collections.emptyList()).build();
        GroupResponseDTO dto = GroupResponseDTO.fromEntity(group);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getOrganizationId());
        assertEquals("N/A", dto.getOrganizationName()); // Default value "N/A"
    }

    @Test
    @DisplayName("fromEntity should handle Group with empty members list")
    void fromEntityShouldHandleGroupWithEmptyMembers() {
        Organization organization = createSampleOrganization(10L, "Test Org");
        Group group = Group.builder().id(1L).name("Empty Members Group").organization(organization).members(Collections.emptyList()).build();
        GroupResponseDTO dto = GroupResponseDTO.fromEntity(group);

        assertNotNull(dto);
        assertNotNull(dto.getMembers());
        assertTrue(dto.getMembers().isEmpty());
    }

    @Test
    @DisplayName("fromEntity should handle Group with null members list (lazy-loaded or not set)")
    void fromEntityShouldHandleGroupWithNullMembers() {
        Organization organization = createSampleOrganization(10L, "Test Org");
        Group group = Group.builder().id(1L).name("Null Members Group").organization(organization).members(null).build(); // Simula lista nula
        GroupResponseDTO dto = GroupResponseDTO.fromEntity(group);

        assertNotNull(dto);
        assertNull(dto.getMembers()); // Espera que a lista de membros seja nula no DTO
    }

    // STUB de MemberResponseDTO para o teste, se você ainda não o forneceu.
    // REMOVA esta classe interna se você já tiver br.edu.fatecsjc.lgnspringapi.dto.MemberResponseDTO.java
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    static class MemberResponseDTO { // static para que possa ser acessada a partir da classe externa
        private Long id;
        private String name;
        private String email;

        public static MemberResponseDTO fromEntity(Member member) {
            if (member == null) {
                return null;
            }
            return MemberResponseDTO.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();
        }
    }
}