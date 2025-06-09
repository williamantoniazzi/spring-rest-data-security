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
    private TypeMap<GroupDTO, Group> typeMapMock; // Mock para o TypeMap

    @InjectMocks
    private GroupConverter groupConverter; // A classe que estamos testando, com mocks injetados

    private Group sampleGroupEntity;
    private GroupDTO sampleGroupDTO;
    private Member sampleMemberEntity1;
    private Member sampleMemberEntity2;
    private MemberDTO sampleMemberDTO1;
    private MemberDTO sampleMemberDTO2;

    @BeforeEach
    void setUp() {
        // Inicializa dados de exemplo para os testes
        sampleMemberEntity1 = Member.builder().id(1L).name("Member A").email("a@test.com").build();
        sampleMemberEntity2 = Member.builder().id(2L).name("Member B").email("b@test.com").build();
        sampleGroupEntity = Group.builder()
                .id(10L)
                .name("Entity Group")
                .members(new ArrayList<>(List.of(sampleMemberEntity1, sampleMemberEntity2)))
                .build();

        sampleMemberDTO1 = MemberDTO.builder().id(101L).name("DTO Member X").email("x@test.com").build();
        sampleMemberDTO2 = MemberDTO.builder().id(102L).name("DTO Member Y").email("y@test.com").build();
        sampleGroupDTO = GroupDTO.builder()
                .id(100L)
                .name("DTO Group")
                .members(new ArrayList<>(List.of(sampleMemberDTO1, sampleMemberDTO2)))
                .build();

        // Configuração inicial para o modelMapper que é usado em convertToEntity(GroupDTO dto)
        // e convertToEntity(GroupDTO dto, Group entity)
        when(modelMapper.createTypeMap(GroupDTO.class, Group.class)).thenReturn(typeMapMock);
        doNothing().when(typeMapMock).addMappings(any());
        doNothing().when(typeMapMock).setProvider(any(Provider.class)); // Captura qualquer Provider
    }

    @Test
    @DisplayName("convertToEntity(GroupDTO dto) should map DTO to new Entity correctly")
    void convertToEntity_shouldMapDtoToNewEntityCorrectly() {
        // Mock do comportamento de mapeamento do ModelMapper para o primeiro método convertToEntity
        when(modelMapper.map(sampleGroupDTO, Group.class)).thenReturn(
                Group.builder()
                        .name(sampleGroupDTO.getName())
                        .members(new ArrayList<>(List.of(
                                Member.builder().id(sampleMemberDTO1.getId()).name(sampleMemberDTO1.getName()).email(sampleMemberDTO1.getEmail()).build(),
                                Member.builder().id(sampleMemberDTO2.getId()).name(sampleMemberDTO2.getName()).email(sampleMemberDTO2.getEmail()).build()
                        )))
                        .build()
        );

        Group result = groupConverter.convertToEntity(sampleGroupDTO);

        assertNotNull(result);
        assertNull(result.getId()); // ID deve ser nulo pois skipamos no mapeamento
        assertEquals(sampleGroupDTO.getName(), result.getName());
        assertNotNull(result.getMembers());
        assertEquals(2, result.getMembers().size());

        // Verifica se o group foi setado em cada membro
        result.getMembers().forEach(member -> assertEquals(result, member.getGroup()));

        // Verifica que o ModelMapper foi chamado corretamente
        verify(modelMapper, times(1)).createTypeMap(GroupDTO.class, Group.class);
        verify(modelMapper, times(1)).map(sampleGroupDTO, Group.class);
    }

    @Test
    @DisplayName("convertToEntity(GroupDTO dto, Group entity) should update existing Entity correctly")
    void convertToEntity_shouldUpdateExistingEntityCorrectly() {
        Group existingEntity = Group.builder().id(99L).name("Existing Group").members(new ArrayList<>()).build();

        // Mock do comportamento de mapeamento do ModelMapper para o segundo método convertToEntity
        // Ele deve retornar uma NOVA instância com base no DTO
        when(modelMapper.map(sampleGroupDTO, Group.class)).thenReturn(
                Group.builder()
                        .name(sampleGroupDTO.getName())
                        .id(existingEntity.getId()) // Mantém o ID da entidade existente
                        .members(new ArrayList<>(List.of(
                                Member.builder().id(sampleMemberDTO1.getId()).name(sampleMemberDTO1.getName()).email(sampleMemberDTO1.getEmail()).build()
                        )))
                        .build()
        );

        // O GroupConverter usa um Provider para a entidade existente, mas o map do ModelMapper ainda cria uma nova instância
        // A lógica do GroupConverter sobrescreve o mapeamento, o que é um pouco peculiar, mas vamos testar o que ele faz.
        // O `Provider<Group> groupProvider = p -> entity;` faz com que o ModelMapper reutilize a instância existente
        // quando `modelMapper.map(dto, Group.class)` é chamado.

        // Simula o comportamento do ModelMapper quando o provider é usado
        when(modelMapper.map(any(GroupDTO.class), eq(Group.class))).thenReturn(existingEntity); // Retorna a entidade existente

        Group result = groupConverter.convertToEntity(sampleGroupDTO, existingEntity);

        assertNotNull(result);
        assertEquals(existingEntity.getId(), result.getId()); // ID deve ser mantido
        assertEquals(sampleGroupDTO.getName(), result.getName());
        assertNotNull(result.getMembers());
        // O mapeamento do ModelMapper padrão não mesclaria listas facilmente,
        // mas o seu código faz um novo mapeamento e seta o grupo.
        // Precisamos mockar o que `modelMapper.map` realmente retorna quando a entidade existente é passada.
        assertEquals(sampleGroupDTO.getMembers().size(), result.getMembers().size()); // Assumindo que os membros do DTO são mapeados

        result.getMembers().forEach(member -> assertEquals(result, member.getGroup()));

        verify(modelMapper, times(2)).createTypeMap(GroupDTO.class, Group.class); // Chamado 2x (um por método)
        verify(modelMapper, times(1)).map(sampleGroupDTO, Group.class); // Chama 1x (para a entidade existente)
    }


    @Test
    @DisplayName("convertToDto should map Entity to DTO correctly")
    void convertToDto_shouldMapEntityToDtoCorrectly() {
        // Mock do comportamento de mapeamento do ModelMapper
        when(modelMapper.map(sampleGroupEntity, GroupDTO.class)).thenReturn(sampleGroupDTO);

        GroupDTO result = groupConverter.convertToDto(sampleGroupEntity);

        assertNotNull(result);
        assertEquals(sampleGroupDTO.getId(), result.getId());
        assertEquals(sampleGroupDTO.getName(), result.getName());
        assertNotNull(result.getMembers());
        assertEquals(sampleGroupDTO.getMembers().size(), result.getMembers().size());
        assertEquals(sampleMemberDTO1.getName(), result.getMembers().get(0).getName());

        verify(modelMapper, times(1)).map(sampleGroupEntity, GroupDTO.class);
    }

    @Test
    @DisplayName("convertToEntity(List<GroupDTO> dtos) should map list of DTOs to list of Entities correctly")
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