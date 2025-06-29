package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarathonRequestDTO Tests")
class MarathonRequestDTOTest {

    private MarathonRequestDTO createSampleMarathonRequestDTO() {
        return MarathonRequestDTO.builder()
                .identification("Request Marathon")
                .weight(50.5)
                .score(7.0)
                .build();
    }

    @Test
    @DisplayName("Should create MarathonRequestDTO using builder and verify getters")
    void shouldCreateMarathonRequestDTOAndVerifyGetters() {
        MarathonRequestDTO dto = createSampleMarathonRequestDTO();

        assertNotNull(dto);
        assertEquals("Request Marathon", dto.getIdentification());
        assertEquals(50.5, dto.getWeight());
        assertEquals(7.0, dto.getScore());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        MarathonRequestDTO dto = new MarathonRequestDTO();
        dto.setIdentification("Updated Request");
        dto.setWeight(60.0);
        dto.setScore(8.0);

        assertEquals("Updated Request", dto.getIdentification());
        assertEquals(60.0, dto.getWeight());
        assertEquals(8.0, dto.getScore());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        MarathonRequestDTO dto = new MarathonRequestDTO("All Args Marathon", 70.0, 9.0);

        assertEquals("All Args Marathon", dto.getIdentification());
        assertEquals(70.0, dto.getWeight());
        assertEquals(9.0, dto.getScore());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        MarathonRequestDTO dto = createSampleMarathonRequestDTO();
        String toStringResult = dto.toString();
        System.out.println(toStringResult);
        String expectedToStringPart = "MarathonRequestDTO(name=Request Marathon, weight=50.5, score=7.0)";
        assertTrue(toStringResult.contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal MarathonRequestDTO objects with equals and hashCode")
    void shouldCompareEqualMarathonRequestDTOs() {
        MarathonRequestDTO dto1 = createSampleMarathonRequestDTO();
        MarathonRequestDTO dto2 = createSampleMarathonRequestDTO();
        MarathonRequestDTO dto3 = MarathonRequestDTO.builder().identification("Different").build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("toEntity should convert MarathonRequestDTO to Marathon entity correctly")
    void toEntityShouldConvertDtoToEntity() {
        MarathonRequestDTO dto = createSampleMarathonRequestDTO();

        Marathon marathon = dto.toEntity();

        assertNotNull(marathon);
        assertNull(marathon.getId()); // ID should be null for new entity from request
        assertEquals("Request Marathon", marathon.getIdentification());
        assertEquals(50.5, marathon.getWeight());
        assertEquals(7.0, marathon.getScore());
    }
}