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
import org.modelmapper.TypeToken;
import org.modelmapper.PropertyMap; // ADICIONADO: Import para PropertyMap
import org.modelmapper.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito para JUnit 5
@DisplayName("GroupConverter Tests")
class GroupConverterTest {

    @Mock
    private ModelMapper modelMapper; // Mock do ModelMapper
    @Mock
    private TypeMap<GroupDTO, Group> typeMapMock;
    @Mock
    private TypeMap<Group, GroupDTO> typeMapEntityToDtoMock; // ADICIONADO: Mock para mapeamento de Entidade para DTO

    @InjectMocks
    private GroupConverter groupConverter; // Classe a ser testada

    // Entidades e DTOs de exemplo para os testes
    private Group sampleGroupEntity;
    private GroupDTO sampleGroupDTO;
    private Member sampleMemberEntity;
    private MemberDTO sampleMemberDTO;


    @BeforeEach
    void setUp() {
        // Inicialização de entidades e DTOs de exemplo
        sampleMemberEntity = Member.builder().id(1L).name("Test Member").age(25).email("test.member@example.com").build();
        sampleMemberDTO = MemberDTO.builder().id(1L).name("Test Member").age(25).email("test.member@example.com").build();

        sampleGroupEntity = Group.builder()
                .id(1L)
                .name("Test Group")
                .members(new ArrayList<>(Collections.singletonList(sampleMemberEntity)))
                .build();

        sampleGroupDTO = GroupDTO.builder()
                .id(1L)
                .name("Test Group")
                .members(new ArrayList<>(Collections.singletonList(sampleMemberDTO)))
                .build();

        // Configuração do ModelMapper para retornar os TypeMaps mockados
        when(modelMapper.createTypeMap(GroupDTO.class, Group.class)).thenReturn(typeMapMock);
        when(modelMapper.createTypeMap(Group.class, GroupDTO.class)).thenReturn(typeMapEntityToDtoMock);

        // Configuração para o TypeMap de DTO para Entidade (GroupDTO -> Group)
        // Isso simula o comportamento do ModelMapper para mapear a lista de membros
        when(typeMapMock.addMappings(any(PropertyMap.class))).thenAnswer(invocation -> {
            // Captura o PropertyMap passado e o executa para simular o mapeamento
            PropertyMap<?, ?> mapping = invocation.getArgument(0);
            mapping.configure(); // Executa a configuração do mapeamento
            return typeMapMock;
        });

        // Configuração para o TypeMap de Entidade para DTO (Group -> GroupDTO)
        when(typeMapEntityToDtoMock.addMappings(any(PropertyMap.class))).thenAnswer(invocation -> {
            PropertyMap<?, ?> mapping = invocation.getArgument(0);
            mapping.configure();
            return typeMapEntityToDtoMock;
        });

        // Configura o mapeamento específico para as listas de membros
        when(modelMapper.map(any(MemberDTO.class), eq(Member.class)))
                .thenReturn(sampleMemberEntity);
        when(modelMapper.map(any(Member.class), eq(MemberDTO.class)))
                .thenReturn(sampleMemberDTO);

        // Chamada ao método de inicialização do converter (se existir e for public/protected)
        groupConverter.init();
    }

    @Test
    @DisplayName("convertToEntity(GroupDTO dto) should map DTO to Entity correctly")
    void convertToEntity_shouldMapDtoToEntityCorrectly() {
        // Configura o mock do ModelMapper para o mapeamento direto de GroupDTO para Group
        when(modelMapper.map(sampleGroupDTO, Group.class)).thenReturn(sampleGroupEntity);

        Group result = groupConverter.convertToEntity(sampleGroupDTO);

        assertNotNull(result);
        assertEquals(sampleGroupEntity.getId(), result.getId());
        assertEquals(sampleGroupEntity.getName(), result.getName());
        assertEquals(sampleGroupEntity.getMembers().size(), result.getMembers().size());
        assertEquals(sampleMemberEntity.getName(), result.getMembers().get(0).getName());

        // Verificando que o método map foi chamado com os argumentos corretos
        verify(modelMapper, times(1)).map(sampleGroupDTO, Group.class);
    }

    @Test
    @DisplayName("convertToDto(Group entity) should map Entity to DTO correctly")
    void convertToDto_shouldMapEntityToDtoCorrectly() {
        // Configura o mock do ModelMapper para o mapeamento direto de Group para GroupDTO
        when(modelMapper.map(sampleGroupEntity, GroupDTO.class)).thenReturn(sampleGroupDTO);

        GroupDTO result = groupConverter.convertToDto(sampleGroupEntity);

        assertNotNull(result);
        assertEquals(sampleGroupDTO.getId(), result.getId());
        assertEquals(sampleGroupDTO.getName(), result.getName());
        assertEquals(sampleGroupDTO.getMembers().size(), result.getMembers().size());
        assertEquals(sampleMemberDTO.getName(), result.getMembers().get(0).getName());

        verify(modelMapper, times(1)).map(sampleGroupEntity, GroupDTO.class);
    }

    @Test
    @DisplayName("convertToEntity(List<GroupDTO> dtoList) should map list of DTOs to list of Entities correctly")
    void convertToEntityList_shouldMapListOfDtosToListOfEntitiesCorrectly() {
        List<GroupDTO> dtoList = Collections.singletonList(sampleGroupDTO);
        List<Group> expectedEntities = Collections.singletonList(sampleGroupEntity);

        // Mock do comportamento de mapeamento de lista do ModelMapper
        when(modelMapper.map(eq(dtoList), any(TypeToken.class))).thenReturn(expectedEntities);

        List<Group> result = groupConverter.convertToEntity(dtoList);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleGroupEntity.getId(), result.get(0).getId());
        assertEquals(sampleGroupEntity.getName(), result.get(0).getName());
        assertEquals(sampleGroupEntity.getMembers().size(), result.get(0).getMembers().size());

        // Verifica se o group foi setado em cada membro nas entidades convertidas
        result.get(0).getMembers().forEach(member -> assertEquals(result.get(0), member.getGroup()));

        verify(modelMapper, times(1)).map(eq(dtoList), any(TypeToken.class));
    }

    @Test
    @DisplayName("convertToDto(List<Group> entities) should map list of Entities to list of DTOs correctly")
    void convertToDtoList_shouldMapListOfEntitiesToListOfDtosCorrectly() {
        List<Group> entityList = Collections.singletonList(sampleGroupEntity);
        List<GroupDTO> expectedDTOs = Collections.singletonList(sampleGroupDTO);

        // Mock do comportamento de mapeamento de lista do ModelMapper
        when(modelMapper.map(eq(entityList), any(TypeToken.class))).thenReturn(expectedDTOs);

        List<GroupDTO> result = groupConverter.convertToDto(entityList);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleGroupDTO.getId(), result.get(0).getId());
        assertEquals(sampleGroupDTO.getName(), result.get(0).getName());
        assertEquals(sampleGroupDTO.getMembers().size(), result.get(0).getMembers().size());

        verify(modelMapper, times(1)).map(eq(entityList), any(TypeToken.class));
    }
}