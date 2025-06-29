package br.edu.fatecsjc.lgnspringapi.converter;

import br.edu.fatecsjc.lgnspringapi.dto.GroupDTO;
import br.edu.fatecsjc.lgnspringapi.dto.MemberDTO;
import br.edu.fatecsjc.lgnspringapi.entity.Group;
import br.edu.fatecsjc.lgnspringapi.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupConverter Tests")
class GroupConverterTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GroupConverter groupConverter;

    private Group sampleGroupEntity;
    private GroupDTO sampleGroupDTO;
    private Member sampleMemberEntity;
    private MemberDTO sampleMemberDTO;

    @BeforeEach
    void setUp() {
        sampleGroupEntity = Group.builder()
                .id(1L)
                .name("Sample Group")
                .members(new ArrayList<>())
                .build();

        sampleMemberEntity = Member.builder()
                .id(101L)
                .name("Sample Member")
                .email("member@example.com")
                .age(30)
                .group(sampleGroupEntity)
                .build();
        sampleGroupEntity.getMembers().add(sampleMemberEntity);

        sampleGroupDTO = GroupDTO.builder()
                .id(1L)
                .name("Sample Group")
                .members(new ArrayList<>())
                .build();

        sampleMemberDTO = MemberDTO.builder()
                .id(101L)
                .name("Sample Member")
                .email("member@example.com")
                .age(30)
                .groupId(1L)
                .build();
        sampleGroupDTO.getMembers().add(sampleMemberDTO);

        // Mock do TypeMap para evitar NullPointerException
        TypeMap<GroupDTO, Group> typeMap = mock(TypeMap.class);
        lenient().when(modelMapper.createTypeMap(GroupDTO.class, Group.class)).thenReturn(typeMap);
        lenient().when(typeMap.addMappings(any(PropertyMap.class))).thenReturn(typeMap);
        lenient().when(typeMap.setProvider(any(Provider.class))).thenReturn(typeMap);
    }

    @Test
    @DisplayName("convertToEntity(List<GroupDTO> dtos) should map list of DTOs to list of Entities correctly")
    void convertToEntityList_shouldMapListOfDtosToListOfEntitiesCorrectly() {
        List<GroupDTO> dtoList = Collections.singletonList(sampleGroupDTO);
        List<Group> expectedEntities = Collections.singletonList(sampleGroupEntity);

        when(modelMapper.map(eq(dtoList), any(java.lang.reflect.Type.class))).thenReturn(expectedEntities);

        List<Group> result = groupConverter.convertToEntity(dtoList);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleGroupEntity.getId(), result.get(0).getId());
        assertEquals(sampleGroupEntity.getName(), result.get(0).getName());
        assertEquals(sampleGroupEntity.getMembers().size(), result.get(0).getMembers().size());
        result.get(0).getMembers().forEach(member -> assertEquals(result.get(0), member.getGroup()));

        verify(modelMapper, times(1)).map(eq(dtoList), any(java.lang.reflect.Type.class));
    }

    @Test
    @DisplayName("convertToDto(List<Group> entities) should map list of Entities to list of DTOs correctly")
    void convertToDtoList_shouldMapListOfEntitiesToListOfDtosCorrectly() {
        List<Group> entityList = Collections.singletonList(sampleGroupEntity);
        List<GroupDTO> expectedDTOs = Collections.singletonList(sampleGroupDTO);

        when(modelMapper.map(eq(entityList), any(java.lang.reflect.Type.class))).thenReturn(expectedDTOs);

        List<GroupDTO> result = groupConverter.convertToDto(entityList);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleGroupDTO.getId(), result.get(0).getId());
        assertEquals(sampleGroupDTO.getName(), result.get(0).getName());
        assertEquals(sampleGroupDTO.getMembers().size(), result.get(0).getMembers().size());

        verify(modelMapper, times(1)).map(eq(entityList), any(java.lang.reflect.Type.class));
    }

    @Test
    @DisplayName("convertToEntity(GroupDTO dto) should map DTO to Entity correctly")
    void convertToEntity_shouldMapDtoToEntityCorrectly() {
        Group expectedEntity = sampleGroupEntity;

        when(modelMapper.map(eq(sampleGroupDTO), eq(Group.class))).thenReturn(expectedEntity);

        Group result = groupConverter.convertToEntity(sampleGroupDTO);

        assertNotNull(result);
        assertEquals(expectedEntity.getId(), result.getId());
        assertEquals(expectedEntity.getName(), result.getName());
        assertEquals(expectedEntity.getMembers().size(), result.getMembers().size());
        result.getMembers().forEach(member -> assertEquals(result, member.getGroup()));

        verify(modelMapper, atLeastOnce()).createTypeMap(GroupDTO.class, Group.class);
        verify(modelMapper, times(1)).map(eq(sampleGroupDTO), eq(Group.class));
    }

    @Test
    @DisplayName("convertToEntity(GroupDTO dto, Group entity) should update existing entity correctly")
    void convertToEntityWithExistingEntity_shouldUpdateExistingEntityCorrectly() {
        Group existingEntity = Group.builder().id(99L).name("Existing Group").members(new ArrayList<>()).build();
        GroupDTO dtoWithUpdates = GroupDTO.builder().id(99L).name("Updated Group").members(new ArrayList<>()).build();

        // Cria uma cópia atualizada para simular o retorno do ModelMapper
        Group updatedEntity = Group.builder()
                .id(existingEntity.getId())
                .name(dtoWithUpdates.getName())
                .members(new ArrayList<>())
                .build();

        when(modelMapper.map(eq(dtoWithUpdates), eq(Group.class))).thenReturn(updatedEntity);

        Group result = groupConverter.convertToEntity(dtoWithUpdates, existingEntity);

        assertNotNull(result);
        assertEquals(existingEntity.getId(), result.getId());
        assertEquals("Updated Group", result.getName());
        assertEquals(0, result.getMembers().size());

        verify(modelMapper, atLeastOnce()).createTypeMap(GroupDTO.class, Group.class);
        verify(modelMapper, times(1)).map(eq(dtoWithUpdates), eq(Group.class));
    }

    @Test
    @DisplayName("convertToDto(Group entity) should map Entity to DTO correctly")
    void convertToDto_shouldMapEntityToDtoCorrectly() {
        GroupDTO expectedDTO = sampleGroupDTO;

        when(modelMapper.map(eq(sampleGroupEntity), eq(GroupDTO.class))).thenReturn(expectedDTO);

        GroupDTO result = groupConverter.convertToDto(sampleGroupEntity);

        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getMembers().size(), result.getMembers().size());

        verify(modelMapper, times(1)).map(eq(sampleGroupEntity), eq(GroupDTO.class));
    }
}