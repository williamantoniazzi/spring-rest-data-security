package br.edu.fatecsjc.lgnspringapi.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Member Entity Tests")
class MemberTest {

    private Group createSampleGroup(Long id, String name) {
        return Group.builder().id(id).name(name).build();
    }

    private Marathon createSampleMarathon(Long id, String name) {
        return Marathon.builder().id(id).identification(name).build();
    }

    private Member createSampleMember() {
        Group group = createSampleGroup(1L, "Group A");
        List<Marathon> marathons = new ArrayList<>();
        marathons.add(createSampleMarathon(101L, "Marathon X"));
        marathons.add(createSampleMarathon(102L, "Marathon Y"));

        return Member.builder()
                .id(1L)
                .name("John Doe")
                .age(30)
                .email("john.doe@example.com")
                .group(group)
                .marathons(marathons)
                .build();
    }

    @Test
    @DisplayName("Should create Member using builder and verify getters")
    void shouldCreateMemberAndVerifyGetters() {
        Member member = createSampleMember();

        assertNotNull(member);
        assertEquals(1L, member.getId());
        assertEquals("John Doe", member.getName());
        assertEquals(30, member.getAge());
        assertEquals("john.doe@example.com", member.getEmail());
        assertNotNull(member.getGroup());
        assertEquals(1L, member.getGroup().getId());
        assertEquals("Group A", member.getGroup().getName());
        assertNotNull(member.getMarathons());
        assertEquals(2, member.getMarathons().size());
        assertEquals("Marathon X", member.getMarathons().get(0).getIdentification());
    }

    @Test
    @DisplayName("Should set new values using setters and verify")
    void shouldSetNewValuesUsingSetters() {
        Member member = new Member();

        member.setId(2L);
        member.setName("Jane Smith");
        member.setAge(25);
        member.setEmail("jane.smith@example.com");
        Group newGroup = createSampleGroup(2L, "Group B");
        member.setGroup(newGroup);
        List<Marathon> newMarathons = new ArrayList<>();
        newMarathons.add(createSampleMarathon(201L, "Marathon Z"));
        member.setMarathons(newMarathons);

        assertEquals(2L, member.getId());
        assertEquals("Jane Smith", member.getName());
        assertEquals(25, member.getAge());
        assertEquals("jane.smith@example.com", member.getEmail());
        assertNotNull(member.getGroup());
        assertEquals(2L, member.getGroup().getId());
        assertEquals("Group B", member.getGroup().getName());
        assertNotNull(member.getMarathons());
        assertEquals(1, member.getMarathons().size());
        assertEquals("Marathon Z", member.getMarathons().get(0).getIdentification());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void shouldTestAllArgsConstructor() {
        Group group = createSampleGroup(3L, "Group C");
        List<Marathon> marathons = new ArrayList<>();
        marathons.add(createSampleMarathon(301L, "Marathon Alpha"));

        Member member = new Member(3L, "All Args Member", 40, "allargs@example.com", group, marathons);

        assertEquals(3L, member.getId());
        assertEquals("All Args Member", member.getName());
        assertEquals(40, member.getAge());
        assertEquals("allargs@example.com", member.getEmail());
        assertNotNull(member.getGroup());
        assertEquals(3L, member.getGroup().getId());
        assertNotNull(member.getMarathons());
        assertEquals(1, member.getMarathons().size());
    }

    @Test
    @DisplayName("Should generate correct toString output")
    void shouldGenerateCorrectToString() {
        Member member = createSampleMember();
        String toStringResult = member.toString();
        System.out.println(toStringResult);

        assertTrue(toStringResult.contains("Member(id=1, name=John Doe"));
        assertTrue(toStringResult.contains("age=30"));
        assertTrue(toStringResult.contains("email=john.doe@example.com"));
        assertTrue(toStringResult.contains("group=Group(id=1, name=Group A"));
        assertTrue(toStringResult.contains("marathons=[Marathon(id=101, identification=Marathon X"));
        assertTrue(toStringResult.contains("Marathon(id=102, identification=Marathon Y"));
    }

    @Test
    @DisplayName("Should compare two equal Member objects with equals and hashCode")
    void shouldCompareEqualMembers() {
        Member member1 = createSampleMember();
        Member member2 = createSampleMember();

        Member member3 = Member.builder()
                .id(99L)
                .name("Different Member")
                .build();

        assertEquals(member1, member2);
        assertEquals(member1.hashCode(), member2.hashCode());

        assertNotEquals(member1, member3);
        assertNotEquals(member1.hashCode(), member3.hashCode());
    }

    @Test
    @DisplayName("Should handle null relationships gracefully")
    void shouldHandleNullRelationships() {
        Member member = Member.builder()
                .id(4L)
                .name("Member without relationships")
                .age(22)
                .email("null@example.com")
                .build();

        assertEquals(4L, member.getId());
        assertEquals("Member without relationships", member.getName());
        assertNull(member.getGroup());
        assertNull(member.getMarathons());
    }
}