package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarathonResponseDTO Tests")
class MarathonResponseDTOTest {

    private MarathonResponseDTO createSampleMarathonResponseDTO() {
        return MarathonResponseDTO.builder()
                .id(1L)
                .identification("Response Marathon")
                .weight(80.0)
                .score(9.2)
                .build();
    }

    @Test
    @DisplayName("Should create MarathonResponseDTO using builder and verify getters")
    void shouldCreateMarathonResponseDTOAndVerifyGetters() {
        MarathonResponseDTO dto = createSampleMarathonResponseDTO();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Response Marathon", dto.getIdentification());
        assertEquals(80.0, dto.getWeight());
        assertEquals(9.2, dto.getScore());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        MarathonResponseDTO dto = new MarathonResponseDTO();
        dto.setId(2L);
        dto.setIdentification("Updated Response");
        dto.setWeight(85.0);
        dto.setScore(9.5);

        assertEquals(2L, dto.getId());
        assertEquals("Updated Response", dto.getIdentification());
        assertEquals(85.0, dto.getWeight());
        assertEquals(9.5, dto.getScore());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        MarathonResponseDTO dto = new MarathonResponseDTO(3L, "All Args Response", 90.0, 9.8);

        assertEquals(3L, dto.getId());
        assertEquals("All Args Response", dto.getIdentification());
        assertEquals(90.0, dto.getWeight());
        assertEquals(9.8, dto.getScore());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        MarathonResponseDTO dto = createSampleMarathonResponseDTO();
        String expectedToStringPart = "MarathonResponseDTO(id=1, identification=Response Marathon, weight=80.0, score=9.2)";
        assertTrue(dto.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal MarathonResponseDTO objects with equals and hashCode")
    void shouldCompareEqualMarathonResponseDTOs() {
        MarathonResponseDTO dto1 = createSampleMarathonResponseDTO();
        MarathonResponseDTO dto2 = createSampleMarathonResponseDTO();
        MarathonResponseDTO dto3 = MarathonResponseDTO.builder().id(99L).identification("Different").build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("fromEntity should convert Marathon entity to MarathonResponseDTO correctly")
    void fromEntityShouldConvertEntityToDto() {
        Marathon marathon = Marathon.builder()
                .id(1L)
                .identification("Entity Marathon")
                .weight(75.5)
                .score(8.9)
                .build();

        MarathonResponseDTO dto = MarathonResponseDTO.fromEntity(marathon);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Entity Marathon", dto.getIdentification());
        assertEquals(75.5, dto.getWeight());
        assertEquals(8.9, dto.getScore());
    }

    @Test
    @DisplayName("fromEntity should return null for null Marathon entity")
    void fromEntityShouldReturnNullForNullEntity() {
        assertNull(MarathonResponseDTO.fromEntity(null));
    }
}