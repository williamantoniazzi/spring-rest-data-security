package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import br.edu.fatecsjc.lgnspringapi.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberResponseDTO Tests")
class MemberResponseDTOTest {

    // Helper para criar uma instância simples de Group (entidade)
    private Group createSampleGroup(Long id, String name) {
        return Group.builder().id(id).name(name).build();
    }

    // Helper para criar uma instância simples de Marathon (entidade)
    // Assume que Marathon.java tem um campo 'name'
    private Marathon createSampleMarathon(Long id, String name) {
        return Marathon.builder().id(id).name(name).build();
    }

    // Helper para criar uma instância simples de MarathonResponseDTO
    // Assume que MarathonResponseDTO.java tem um campo 'name'
    private MarathonResponseDTO createSampleMarathonResponseDTO(Long id, String name) {
        return MarathonResponseDTO.builder().id(id).name(name).build();
    }

    private MemberResponseDTO createSampleMemberResponseDTO() {
        return MemberResponseDTO.builder()
                .id(1L)
                .name("Response Member")
                .age(30)
                .email("resp.member@example.com") // ADICIONADO: Email no builder
                .groupId(10L)
                .groupName("Response Group")
                .marathons(List.of(createSampleMarathonResponseDTO(101L, "Resp Marathon 1")))
                .build();
    }

    @Test
    @DisplayName("Should create MemberResponseDTO using builder and verify getters")
    void shouldCreateMemberResponseDTOAndVerifyGetters() {
        MemberResponseDTO dto = createSampleMemberResponseDTO();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Response Member", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("resp.member@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertEquals(10L, dto.getGroupId());
        assertEquals("Response Group", dto.getGroupName());
        assertNotNull(dto.getMarathons());
        assertEquals(1, dto.getMarathons().size());
        assertEquals("Resp Marathon 1", dto.getMarathons().get(0).getName());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setId(2L);
        dto.setName("Updated Response Member");
        dto.setAge(32);
        dto.setEmail("updated.resp@example.com"); // ADICIONADO: Set do email
        dto.setGroupId(20L);
        dto.setGroupName("Updated Group");
        dto.setMarathons(List.of(createSampleMarathonResponseDTO(201L, "Resp Marathon 2")));

        assertEquals(2L, dto.getId());
        assertEquals("Updated Response Member", dto.getName());
        assertEquals(32, dto.getAge());
        assertEquals("updated.resp@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertEquals(20L, dto.getGroupId());
        assertEquals("Updated Group", dto.getGroupName());
        assertNotNull(dto.getMarathons());
        assertEquals(1, dto.getMarathons().size());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        List<MarathonResponseDTO> marathons = List.of(createSampleMarathonResponseDTO(301L, "Resp Marathon 3"));
        // ADICIONADO: Email no construtor completo
        MemberResponseDTO dto = new MemberResponseDTO(3L, "All Args Response Member", 35, "allargs.resp@example.com", 30L, "All Args Group", marathons);

        assertEquals(3L, dto.getId());
        assertEquals("All Args Response Member", dto.getName());
        assertEquals(35, dto.getAge());
        assertEquals("allargs.resp@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertEquals(30L, dto.getGroupId());
        assertEquals("All Args Group", dto.getGroupName());
        assertNotNull(dto.getMarathons());
        assertEquals(1, dto.getMarathons().size());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        MemberResponseDTO dto = createSampleMemberResponseDTO();
        // O toString já estava esperando o email, apenas confirmando a estrutura
        String expectedToStringPart = "MemberResponseDTO(id=1, name=Response Member, age=30, email=resp.member@example.com, groupId=10, groupName=Response Group, marathons=[MarathonResponseDTO(id=101, name=Resp Marathon 1)";
        assertTrue(dto.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal MemberResponseDTO objects with equals and hashCode")
    void shouldCompareEqualMemberResponseDTOs() {
        MemberResponseDTO dto1 = createSampleMemberResponseDTO();
        // Recria dto2 com os mesmos valores, incluindo email, para garantir a igualdade completa
        MemberResponseDTO dto2 = MemberResponseDTO.builder()
                .id(1L)
                .name("Response Member")
                .age(30)
                .email("resp.member@example.com")
                .groupId(10L)
                .groupName("Response Group")
                .marathons(List.of(createSampleMarathonResponseDTO(101L, "Resp Marathon 1")))
                .build();

        MemberResponseDTO dto3 = MemberResponseDTO.builder()
                .id(99L)
                .name("Different")
                .age(99)
                .email("different.resp@example.com")
                .groupId(99L)
                .groupName("Different Group")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("fromEntity should convert Member to MemberResponseDTO correctly")
    void fromEntityShouldConvertMemberToDto() {
        Group group = createSampleGroup(10L, "Test Group");
        Marathon marathon1 = createSampleMarathon(101L, "Marathon A");
        Marathon marathon2 = createSampleMarathon(102L, "Marathon B");

        Member member = Member.builder()
                .id(1L)
                .name("Entity Member")
                .age(25)
                .email("entity.member@example.com") // ADICIONADO: Email na entidade Member
                .group(group)
                .marathons(List.of(marathon1, marathon2))
                .build();

        MemberResponseDTO dto = MemberResponseDTO.fromEntity(member);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Entity Member", dto.getName());
        assertEquals(25, dto.getAge());
        assertEquals("entity.member@example.com", dto.getEmail()); // ADICIONADO: Verificação do email no DTO
        assertEquals(10L, dto.getGroupId());
        assertEquals("Test Group", dto.getGroupName());
        assertNotNull(dto.getMarathons());
        assertEquals(2, dto.getMarathons().size());
        assertEquals("Marathon A", dto.getMarathons().get(0).getName());
        assertEquals("Marathon B", dto.getMarathons().get(1).getName());
    }

    @Test
    @DisplayName("fromEntity should handle null Member input")
    void fromEntityShouldHandleNullMember() {
        assertNull(MemberResponseDTO.fromEntity(null));
    }

    @Test
    @DisplayName("fromEntity should handle Member with null Group")
    void fromEntityShouldHandleMemberWithNullGroup() {
        Member member = Member.builder()
                .id(1L)
                .name("No Group Member")
                .age(20)
                .email("no.group@example.com") // ADICIONADO: Email na entidade Member
                .marathons(Collections.emptyList())
                .build();
        MemberResponseDTO dto = MemberResponseDTO.fromEntity(member);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("no.group@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertNull(dto.getGroupId());
        assertNull(dto.getGroupName());
    }

    @Test
    @DisplayName("fromEntity should handle Member with empty marathons list")
    void fromEntityShouldHandleMemberWithEmptyMarathons() {
        Group group = createSampleGroup(10L, "Test Group");
        Member member = Member.builder()
                .id(1L)
                .name("Empty Marathons Member")
                .age(20)
                .email("empty.marathons@example.com") // ADICIONADO: Email na entidade Member
                .group(group)
                .marathons(Collections.emptyList())
                .build();
        MemberResponseDTO dto = MemberResponseDTO.fromEntity(member);

        assertNotNull(dto);
        assertEquals("empty.marathons@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertNotNull(dto.getMarathons());
        assertTrue(dto.getMarathons().isEmpty());
    }

    @Test
    @DisplayName("fromEntity should handle Member with null marathons list")
    void fromEntityShouldHandleMemberWithNullMarathons() {
        Group group = createSampleGroup(10L, "Test Group");
        Member member = Member.builder()
                .id(1L)
                .name("Null Marathons Member")
                .age(20)
                .email("null.marathons@example.com") // ADICIONADO: Email na entidade Member
                .group(group)
                .marathons(null) // Simula lista nula
                .build();
        MemberResponseDTO dto = MemberResponseDTO.fromEntity(member);

        assertNotNull(dto);
        assertEquals("null.marathons@example.com", dto.getEmail()); // ADICIONADO: Verificação do email
        assertNull(dto.getMarathons());
    }
}