package br.edu.fatecsjc.lgnspringapi.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberDTO Tests")
class MemberDTOTest {

    private MemberDTO createSampleMemberDTO() {
        return MemberDTO.builder()
                .id(1L)
                .name("Member DTO Name")
                .age(28)
                .email("dto.member@example.com")
                .build();
    }

    @Test
    @DisplayName("Should create MemberDTO using builder and verify getters")
    void shouldCreateMemberDTOAndVerifyGetters() {
        MemberDTO memberDTO = createSampleMemberDTO();

        assertNotNull(memberDTO);
        assertEquals(1L, memberDTO.getId());
        assertEquals("Member DTO Name", memberDTO.getName());
        assertEquals(28, memberDTO.getAge());
        assertEquals("dto.member@example.com", memberDTO.getEmail());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(2L);
        memberDTO.setName("Updated DTO Name");
        memberDTO.setAge(35);
        memberDTO.setEmail("updated.dto@example.com");

        assertEquals(2L, memberDTO.getId());
        assertEquals("Updated DTO Name", memberDTO.getName());
        assertEquals(35, memberDTO.getAge());
        assertEquals("updated.dto@example.com", memberDTO.getEmail());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        MemberDTO memberDTO = new MemberDTO(3L, "All Args Member", 40, "allargs@example.com");

        assertEquals(3L, memberDTO.getId());
        assertEquals("All Args Member", memberDTO.getName());
        assertEquals(40, memberDTO.getAge());
        assertEquals("allargs@example.com", memberDTO.getEmail());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        MemberDTO memberDTO = createSampleMemberDTO();
        String expectedToStringPart = "MemberDTO(id=1, name=Member DTO Name, age=28, email=dto.member@example.com)";
        assertTrue(memberDTO.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal MemberDTO objects with equals and hashCode")
    void shouldCompareEqualMemberDTOs() {
        MemberDTO dto1 = createSampleMemberDTO();
        MemberDTO dto2 = createSampleMemberDTO();
        MemberDTO dto3 = MemberDTO.builder().id(99L).name("Different").build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}