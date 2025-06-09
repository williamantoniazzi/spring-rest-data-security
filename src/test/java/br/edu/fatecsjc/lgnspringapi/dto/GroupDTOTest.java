package br.edu.fatecsjc.lgnspringapi.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GroupDTO Tests")
class GroupDTOTest {

    // Helper para criar um MemberDTO simples
    private MemberDTO createSampleMemberDTO(Long id, String name) {
        return MemberDTO.builder().id(id).name(name).build();
    }

    private GroupDTO createSampleGroupDTO() {
        return GroupDTO.builder()
                .id(1L)
                .name("Sample Group")
                .members(List.of(createSampleMemberDTO(101L, "Member One"), createSampleMemberDTO(102L, "Member Two")))
                .build();
    }

    @Test
    @DisplayName("Should create GroupDTO using builder and verify getters")
    void shouldCreateGroupDTOAndVerifyGetters() {
        GroupDTO groupDTO = createSampleGroupDTO();

        assertNotNull(groupDTO);
        assertEquals(1L, groupDTO.getId());
        assertEquals("Sample Group", groupDTO.getName());
        assertNotNull(groupDTO.getMembers());
        assertEquals(2, groupDTO.getMembers().size());
        assertEquals("Member One", groupDTO.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(2L);
        groupDTO.setName("Updated Group");
        groupDTO.setMembers(Collections.singletonList(createSampleMemberDTO(201L, "New Member")));

        assertEquals(2L, groupDTO.getId());
        assertEquals("Updated Group", groupDTO.getName());
        assertNotNull(groupDTO.getMembers());
        assertEquals(1, groupDTO.getMembers().size());
        assertEquals("New Member", groupDTO.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        List<MemberDTO> members = List.of(createSampleMemberDTO(301L, "Member Three"));
        GroupDTO groupDTO = new GroupDTO(3L, "All Args Group", members);

        assertEquals(3L, groupDTO.getId());
        assertEquals("All Args Group", groupDTO.getName());
        assertNotNull(groupDTO.getMembers());
        assertEquals(1, groupDTO.getMembers().size());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        GroupDTO groupDTO = createSampleGroupDTO();
        String expectedToStringPart = "GroupDTO(id=1, name=Sample Group, members=[MemberDTO(id=101, name=Member One)";
        assertTrue(groupDTO.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal GroupDTO objects with equals and hashCode")
    void shouldCompareEqualGroupDTOs() {
        GroupDTO groupDTO1 = createSampleGroupDTO();
        GroupDTO groupDTO2 = createSampleGroupDTO();
        GroupDTO groupDTO3 = GroupDTO.builder().id(99L).name("Different Group").build();

        assertEquals(groupDTO1, groupDTO2);
        assertEquals(groupDTO1.hashCode(), groupDTO2.hashCode());
        assertNotEquals(groupDTO1, groupDTO3);
        assertNotEquals(groupDTO1.hashCode(), groupDTO3.hashCode());
    }

    @Test
    @DisplayName("Should handle null members list")
    void shouldHandleNullMembersList() {
        GroupDTO groupDTO = GroupDTO.builder().id(4L).name("Group with null members").build();
        assertNull(groupDTO.getMembers());
    }

    @Test
    @DisplayName("Should handle empty members list")
    void shouldHandleEmptyMembersList() {
        GroupDTO groupDTO = GroupDTO.builder().id(5L).name("Group with empty members").members(Collections.emptyList()).build();
        assertNotNull(groupDTO.getMembers());
        assertTrue(groupDTO.getMembers().isEmpty());
    }
}

// STUB de MemberDTO para o teste, se você ainda não o forneceu.
// REMOVA esta classe interna se você já tiver br.edu.fatecsjc.lgnspringapi.dto.MemberDTO.java
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
class MemberDTO {
    private Long id;
    private String name;
    private String email;
}