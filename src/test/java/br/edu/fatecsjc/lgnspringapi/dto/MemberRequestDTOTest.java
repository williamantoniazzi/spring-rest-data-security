package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import br.edu.fatecsjc.lgnspringapi.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberRequestDTO Tests")
class MemberRequestDTOTest {

    // Helper para criar uma instância simples de Group (entidade)
    private Group createSampleGroup(Long id, String name) {
        return Group.builder().id(id).name(name).build();
    }

    // Helper para criar uma instância simples de Marathon (entidade)
    private Marathon createSampleMarathon(Long id, String name) {
        return Marathon.builder().id(id).name(name).build();
    }

    private MemberRequestDTO createSampleMemberRequestDTO() {
        return MemberRequestDTO.builder()
                .name("Requested Member")
                .age(20)
                .email("req.member@example.com")
                .groupId(1L)
                .marathonIds(List.of(101L, 102L))
                .build();
    }

    @Test
    @DisplayName("Should create MemberRequestDTO using builder and verify getters")
    void shouldCreateMemberRequestDTOAndVerifyGetters() {
        MemberRequestDTO dto = createSampleMemberRequestDTO();

        assertNotNull(dto);
        assertEquals("Requested Member", dto.getName());
        assertEquals(20, dto.getAge());
        assertEquals("req.member@example.com", dto.getEmail());
        assertEquals(1L, dto.getGroupId());
        assertNotNull(dto.getMarathonIds());
        assertEquals(2, dto.getMarathonIds().size());
        assertTrue(dto.getMarathonIds().contains(101L));
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        MemberRequestDTO dto = new MemberRequestDTO();
        dto.setName("Updated Request Member");
        dto.setAge(22);
        dto.setEmail("updated.req@example.com");
        dto.setGroupId(2L);
        dto.setMarathonIds(Collections.singletonList(201L));

        assertEquals("Updated Request Member", dto.getName());
        assertEquals(22, dto.getAge());
        assertEquals("updated.req@example.com", dto.getEmail());
        assertEquals(2L, dto.getGroupId());
        assertNotNull(dto.getMarathonIds());
        assertEquals(1, dto.getMarathonIds().size());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        List<Long> marathonIds = List.of(301L);
        MemberRequestDTO dto = new MemberRequestDTO("All Args Member", 25, "allargs@example.com", 3L, marathonIds);

        assertEquals("All Args Member", dto.getName());
        assertEquals(25, dto.getAge());
        assertEquals("allargs@example.com", dto.getEmail());
        assertEquals(3L, dto.getGroupId());
        assertNotNull(dto.getMarathonIds());
        assertEquals(1, dto.getMarathonIds().size());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        MemberRequestDTO dto = createSampleMemberRequestDTO();
        String expectedToStringPart = "MemberRequestDTO(name=Requested Member, age=20, email=req.member@example.com, groupId=1, marathonIds=[101, 102])";
        assertTrue(dto.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal MemberRequestDTO objects with equals and hashCode")
    void shouldCompareEqualMemberRequestDTOs() {
        MemberRequestDTO dto1 = createSampleMemberRequestDTO();
        MemberRequestDTO dto2 = createSampleMemberRequestDTO();
        MemberRequestDTO dto3 = MemberRequestDTO.builder().name("Different").groupId(99L).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("toEntity should convert MemberRequestDTO to Member entity correctly")
    void toEntityShouldConvertDtoToEntity() {
        MemberRequestDTO dto = createSampleMemberRequestDTO();
        Group group = createSampleGroup(1L, "Test Group");
        List<Marathon> marathons = List.of(createSampleMarathon(101L, "M1"), createSampleMarathon(102L, "M2"));

        Member member = dto.toEntity(group, marathons);

        assertNotNull(member);
        assertNull(member.getId()); // ID should be null for new entity
        assertEquals("Requested Member", member.getName());
        assertEquals(20, member.getAge());
        assertEquals("req.member@example.com", member.getEmail());
        assertNotNull(member.getGroup());
        assertEquals(group.getId(), member.getGroup().getId());
        assertNotNull(member.getMarathons());
        assertEquals(2, member.getMarathons().size());
        assertEquals(marathons.get(0).getName(), member.getMarathons().get(0).getName());
    }

    @Test
    @DisplayName("toEntity should handle null marathons list")
    void toEntityShouldHandleNullMarathons() {
        MemberRequestDTO dto = MemberRequestDTO.builder()
                .name("No Marathon Member")
                .age(20)
                .email("nomarathon@example.com")
                .groupId(1L)
                .marathonIds(null) // Null marathonIds
                .build();
        Group group = createSampleGroup(1L, "Test Group");

        Member member = dto.toEntity(group, null); // Pass null marathons list

        assertNotNull(member);
        assertNull(member.getMarathons());
    }

    @Test
    @DisplayName("toEntity should handle empty marathons list")
    void toEntityShouldHandleEmptyMarathons() {
        MemberRequestDTO dto = MemberRequestDTO.builder()
                .name("Empty Marathon Member")
                .age(20)
                .email("emptymarathon@example.com")
                .groupId(1L)
                .marathonIds(Collections.emptyList()) // Empty marathonIds
                .build();
        Group group = createSampleGroup(1L, "Test Group");

        Member member = dto.toEntity(group, Collections.emptyList()); // Pass empty marathons list

        assertNotNull(member);
        assertNotNull(member.getMarathons());
        assertTrue(member.getMarathons().isEmpty());
    }

    @Test
    @DisplayName("toEntity should throw IllegalArgumentException when group is null")
    void toEntityShouldThrowExceptionWhenGroupIsNull() {
        MemberRequestDTO dto = createSampleMemberRequestDTO();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dto.toEntity(null, List.of());
        });

        assertEquals("O grupo não pode ser nulo ao converter MemberRequestDTO para entidade.", exception.getMessage());
    }
}