package br.edu.fatecsjc.lgnspringapi.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Marathon Entity Tests")
class MarathonTest {

    private Marathon createSampleMarathon() {
        return Marathon.builder()
                .id(1L)
                .identification("Run São Silvestre")
                .weight(75.5)
                .score(8.9)
                .build();
    }

    @Test
    @DisplayName("Should create Marathon using builder and verify getters")
    void shouldCreateMarathonAndVerifyGetters() {
        Marathon marathon = createSampleMarathon();

        assertNotNull(marathon);
        assertEquals(1L, marathon.getId());
        assertEquals("Run São Silvestre", marathon.getIdentification());
        assertEquals(75.5, marathon.getWeight());
        assertEquals(8.9, marathon.getScore());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        Marathon marathon = new Marathon(); // Usa o construtor NoArgsConstructor

        marathon.setId(2L);
        marathon.setIdentification("Ironman Brazil");
        marathon.setWeight(100.0);
        marathon.setScore(9.5);

        assertEquals(2L, marathon.getId());
        assertEquals("Ironman Brazil", marathon.getIdentification());
        assertEquals(100.0, marathon.getWeight());
        assertEquals(9.5, marathon.getScore());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        Marathon marathon = new Marathon(3L, "Marathon Rio", 60.0, 7.8);

        assertEquals(3L, marathon.getId());
        assertEquals("Marathon Rio", marathon.getIdentification());
        assertEquals(60.0, marathon.getWeight());
        assertEquals(7.8, marathon.getScore());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        Marathon marathon = createSampleMarathon();
        String expectedToStringPart = "Marathon(id=1, identification=Run São Silvestre, weight=75.5, score=8.9)";
        assertTrue(marathon.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal Marathon objects with equals and hashCode")
    void shouldCompareEqualMarathons() {
        Marathon marathon1 = createSampleMarathon();
        Marathon marathon2 = createSampleMarathon();

        Marathon marathon3 = Marathon.builder()
                .id(99L)
                .identification("Different Marathon")
                .weight(1.0)
                .score(1.0)
                .build();

        assertEquals(marathon1, marathon2); // equals()
        assertEquals(marathon1.hashCode(), marathon2.hashCode()); // hashCode()

        assertNotEquals(marathon1, marathon3);
        assertNotEquals(marathon1.hashCode(), marathon3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValues() {
        Marathon marathon = Marathon.builder().id(5L).build(); // Apenas ID

        assertEquals(5L, marathon.getId());
        assertNull(marathon.getIdentification());
        assertNull(marathon.getWeight());
        assertNull(marathon.getScore());

        marathon.setIdentification(null);
        marathon.setWeight(null);
        marathon.setScore(null);

        assertNull(marathon.getIdentification());
        assertNull(marathon.getWeight());
        assertNull(marathon.getScore());
    }
}