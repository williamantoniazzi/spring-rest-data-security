package br.edu.fatecsjc.lgnspringapi.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AddressDTO Tests")
class AddressDTOTest {

    // Método para criar uma instância de AddressDTO para reutilização
    private AddressDTO createSampleAddressDTO() {
        return AddressDTO.builder()
                .street("Rua DTO Exemplo")
                .number("10")
                .neighborhood("Vila DTO")
                .zipCode("00000-000")
                .city("Cidade DTO")
                .state("DT")
                .build();
    }

    @Test
    @DisplayName("Should create AddressDTO using builder and verify getters")
    void shouldCreateAddressDTOAndVerifyGetters() {
        AddressDTO addressDTO = createSampleAddressDTO();

        assertNotNull(addressDTO);
        assertEquals("Rua DTO Exemplo", addressDTO.getStreet());
        assertEquals("10", addressDTO.getNumber());
        assertEquals("Vila DTO", addressDTO.getNeighborhood());
        assertEquals("00000-000", addressDTO.getZipCode());
        assertEquals("Cidade DTO", addressDTO.getCity());
        assertEquals("DT", addressDTO.getState());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("Avenida Nova DTO");
        addressDTO.setNumber("20");
        addressDTO.setNeighborhood("Bairro Novo DTO");
        addressDTO.setZipCode("11111-111");
        addressDTO.setCity("Outra Cidade DTO");
        addressDTO.setState("OT");

        assertEquals("Avenida Nova DTO", addressDTO.getStreet());
        assertEquals("20", addressDTO.getNumber());
        assertEquals("Bairro Novo DTO", addressDTO.getNeighborhood());
        assertEquals("11111-111", addressDTO.getZipCode());
        assertEquals("Outra Cidade DTO", addressDTO.getCity());
        assertEquals("OT", addressDTO.getState());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        AddressDTO addressDTO = new AddressDTO("Rua AllArgs", "30", "Centro All", "22222-222", "Cidade All", "AL");

        assertEquals("Rua AllArgs", addressDTO.getStreet());
        assertEquals("30", addressDTO.getNumber());
        assertEquals("Centro All", addressDTO.getNeighborhood());
        assertEquals("22222-222", addressDTO.getZipCode());
        assertEquals("Cidade All", addressDTO.getCity());
        assertEquals("AL", addressDTO.getState());
    }

    @Test
    @DisplayName("Should generate correct toString output for AddressDTO")
    void shouldGenerateCorrectToString() {
        AddressDTO addressDTO = createSampleAddressDTO();
        String expectedToStringPart = "AddressDTO(street=Rua DTO Exemplo, number=10, neighborhood=Vila DTO, zipCode=00000-000, city=Cidade DTO, state=DT)";
        assertTrue(addressDTO.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal AddressDTO objects with equals and hashCode")
    void shouldCompareEqualAddressDTOs() {
        AddressDTO addressDTO1 = createSampleAddressDTO();
        AddressDTO addressDTO2 = createSampleAddressDTO();
        AddressDTO addressDTO3 = AddressDTO.builder()
                .street("Outra Rua DTO")
                .number("99")
                .build();

        assertEquals(addressDTO1, addressDTO2);
        assertEquals(addressDTO1.hashCode(), addressDTO2.hashCode());

        assertNotEquals(addressDTO1, addressDTO3);
        assertNotEquals(addressDTO1.hashCode(), addressDTO3.hashCode());
    }
}