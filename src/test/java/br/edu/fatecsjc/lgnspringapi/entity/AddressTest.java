package br.edu.fatecsjc.lgnspringapi.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Entity Tests") // Nome de exibição para o teste
class AddressTest {

    // Método para criar uma instância de Address para reutilização
    private Address createSampleAddress() {
        return Address.builder()
                .street("Rua Exemplo")
                .number("123")
                .neighborhood("Centro")
                .zipCode("12345-678")
                .city("São José dos Campos")
                .state("SP")
                .build();
    }

    @Test
    @DisplayName("Should create Address using builder and verify getters")
    void shouldCreateAddressAndVerifyGetters() {
        Address address = createSampleAddress();

        // Verifica se os valores foram corretamente atribuídos via builder e acessados via getters
        assertNotNull(address);
        assertEquals("Rua Exemplo", address.getStreet());
        assertEquals("123", address.getNumber());
        assertEquals("Centro", address.getNeighborhood());
        assertEquals("12345-678", address.getZipCode());
        assertEquals("São José dos Campos", address.getCity());
        assertEquals("SP", address.getState());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        Address address = new Address(); // Usa o construtor NoArgsConstructor
        address.setStreet("Avenida Nova");
        address.setNumber("456");
        address.setNeighborhood("Bairro Legal");
        address.setZipCode("87654-321");
        address.setCity("Jacareí");
        address.setState("SP");

        // Verifica se os valores foram corretamente modificados via setters
        assertEquals("Avenida Nova", address.getStreet());
        assertEquals("456", address.getNumber());
        assertEquals("Bairro Legal", address.getNeighborhood());
        assertEquals("87654-321", address.getZipCode());
        assertEquals("Jacareí", address.getCity());
        assertEquals("SP", address.getState());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        Address address = new Address("Rua Completa", "789", "Vila", "98765-432", "Taubaté", "SP");

        // Verifica se o construtor AllArgsConstructor funciona
        assertEquals("Rua Completa", address.getStreet());
        assertEquals("789", address.getNumber());
        assertEquals("Vila", address.getNeighborhood());
        assertEquals("98765-432", address.getZipCode());
        assertEquals("Taubaté", address.getCity());
        assertEquals("SP", address.getState());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        Address address = createSampleAddress();
        // Verifica se o toString não é nulo e contém partes esperadas (bom para depuração)
        String expectedToStringPart = "Address(street=Rua Exemplo, number=123, neighborhood=Centro, zipCode=12345-678, city=São José dos Campos, state=SP)";
        assertTrue(address.toString().contains(expectedToStringPart));
    }

    @Test
    @DisplayName("Should compare two equal Address objects with equals and hashCode")
    void shouldCompareEqualAddresses() {
        Address address1 = createSampleAddress();
        Address address2 = createSampleAddress();
        Address address3 = Address.builder()
                .street("Outra Rua")
                .number("1")
                .build();

        // Verifica a igualdade
        assertEquals(address1, address2); // equals()
        assertEquals(address1.hashCode(), address2.hashCode()); // hashCode()

        // Verifica a desigualdade
        assertNotEquals(address1, address3);
        assertNotEquals(address1.hashCode(), address3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValues() {
        Address address = Address.builder().build(); // Todos os campos nulos
        assertNull(address.getStreet());
        assertNull(address.getCity());
        assertNotNull(address.toString()); // toString ainda deve funcionar
    }
}
