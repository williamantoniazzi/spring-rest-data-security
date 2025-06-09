package br.edu.fatecsjc.lgnspringapi.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Group Entity Tests")
class GroupTest {

    // Método auxiliar para criar uma instância de Organization
    private Organization createSampleOrganization(Long id, String name) {
        return Organization.builder()
                .id(id)
                .name(name)
                .cnpj("12.345.678/0001-90")
                .address(Address.builder().street("Org Street").city("Org City").build())
                .build();
    }

    // Método auxiliar para criar uma instância de Member
    private Member createSampleMember(Long id, String name, String email) {
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    // Método auxiliar para criar uma instância de Group para reutilização
    private Group createSampleGroup() {
        Organization org = createSampleOrganization(1L, "Org Teste");
        List<Member> members = new ArrayList<>();
        members.add(createSampleMember(101L, "Member A", "membera@example.com"));
        members.add(createSampleMember(102L, "Member B", "memberb@example.com"));

        return Group.builder()
                .id(1L)
                .name("Grupo de Teste")
                .organization(org)
                .members(members)
                .build();
    }

    @Test
    @DisplayName("Should create Group using builder and verify getters")
    void shouldCreateGroupAndVerifyGetters() {
        Group group = createSampleGroup();

        assertNotNull(group);
        assertEquals(1L, group.getId());
        assertEquals("Grupo de Teste", group.getName());
        assertNotNull(group.getOrganization());
        assertEquals(1L, group.getOrganization().getId());
        assertEquals("Org Teste", group.getOrganization().getName());
        assertNotNull(group.getMembers());
        assertEquals(2, group.getMembers().size());
        assertEquals("Member A", group.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        Group group = new Group(); // Usa o construtor NoArgsConstructor

        group.setId(2L);
        group.setName("Grupo Modificado");
        Organization newOrg = createSampleOrganization(2L, "Nova Org");
        group.setOrganization(newOrg);

        List<Member> newMembers = new ArrayList<>();
        newMembers.add(createSampleMember(201L, "Member C", "memberc@example.com"));
        group.setMembers(newMembers);

        assertEquals(2L, group.getId());
        assertEquals("Grupo Modificado", group.getName());
        assertNotNull(group.getOrganization());
        assertEquals(2L, group.getOrganization().getId());
        assertEquals("Nova Org", group.getOrganization().getName());
        assertNotNull(group.getMembers());
        assertEquals(1, group.getMembers().size());
        assertEquals("Member C", group.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        Organization org = createSampleOrganization(3L, "Org AllArgs");
        List<Member> members = new ArrayList<>();
        members.add(createSampleMember(301L, "Member D", "memberd@example.com"));

        Group group = new Group(3L, "Grupo AllArgs", org, members);

        assertEquals(3L, group.getId());
        assertEquals("Grupo AllArgs", group.getName());
        assertNotNull(group.getOrganization());
        assertEquals(3L, group.getOrganization().getId());
        assertEquals("Org AllArgs", group.getOrganization().getName());
        assertNotNull(group.getMembers());
        assertEquals(1, group.getMembers().size());
        assertEquals("Member D", group.getMembers().get(0).getName());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        Group group = createSampleGroup();
        // Verifica se o toString não é nulo e contém partes esperadas
        // Nota: O toString do Lombok para entidades com relacionamentos pode ser recursivo ou truncado.
        // É importante garantir que o toString não entre em loop infinito com referências circulares.
        // O Lombok normalmente lida bem com isso, mas é um ponto a observar.
        String expectedToStringPart = "Group(id=1, name=Grupo de Teste"; // Verifique o início do toString gerado.
        assertTrue(group.toString().contains(expectedToStringPart));
        // Para collections, o Lombok não inclui o conteúdo inteiro por padrão, apenas o tipo da coleção e o tamanho.
        assertTrue(group.toString().contains("members=[Member(id=101")); // Verifique a primeira parte do membro
    }

    @Test
    @DisplayName("Should compare two equal Group objects with equals and hashCode")
    void shouldCompareEqualGroups() {
        Group group1 = createSampleGroup();
        Group group2 = createSampleGroup();

        // Crie um grupo diferente
        Group group3 = Group.builder()
                .id(99L)
                .name("Grupo Diferente")
                .build();

        // Verifica a igualdade
        assertEquals(group1, group2); // equals()
        assertEquals(group1.hashCode(), group2.hashCode()); // hashCode()

        // Verifica a desigualdade
        assertNotEquals(group1, group3);
        assertNotEquals(group1.hashCode(), group3.hashCode());
    }

    @Test
    @DisplayName("Should handle null relationships gracefully")
    void shouldHandleNullRelationships() {
        Group group = Group.builder()
                .id(4L)
                .name("Grupo Sem Relacionamentos")
                .build();

        assertEquals(4L, group.getId());
        assertEquals("Grupo Sem Relacionamentos", group.getName());
        assertNull(group.getOrganization()); // Organização deve ser nula
        assertNull(group.getMembers());     // Membros devem ser nulos
    }
}