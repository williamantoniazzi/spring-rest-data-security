package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group; // Importa a entidade Group
import br.edu.fatecsjc.lgnspringapi.entity.Member; // Importa a entidade Member (para a lista de membros)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class GroupResponseDTO {
    private Long id;
    private String name;
    private Long organizationId; // Apenas o ID da organização para evitar recursão
    private String organizationName; // Opcional: Adicionar o nome da organização para facilitar a visualização
    private List<MemberResponseDTO> members; // Para mostrar os membros associados (usando o DTO de Member)

    /**
     * Converte uma entidade Group em um DTO de resposta.
     * Gerencia o carregamento lazy da lista de membros.
     * @param group A entidade Group a ser convertida.
     * @return Uma nova instância de GroupResponseDTO.
     */
    public static GroupResponseDTO fromEntity(Group group) {
        if (group == null) {
            return null; // Retorna nulo se a entidade for nula
        }

        List<MemberResponseDTO> memberDTOs = null;
        // Verifica se a coleção de membros foi inicializada para evitar LazyInitializationException
        if (group.getMembers() != null && !group.getMembers().isEmpty() && group.getMembers().get(0) != null) {
            // Se a coleção estiver inicializada e não vazia, mapeia os membros.
            // A verificação 'get(0) != null' é um truque para forçar a inicialização lazy sem tentar carregar tudo se for realmente null.
            // O ideal é que a camada de serviço garanta o carregamento adequado antes do mapeamento, se necessário.
            memberDTOs = group.getMembers().stream()
                    .map(MemberResponseDTO::fromEntity) // Chama o mapeamento de Member para cada Member
                    .collect(Collectors.toList());
        }
        // Se a coleção estiver vazia ou não inicializada, 'memberDTOs' permanecerá nulo.

        return GroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .organizationId(group.getOrganization() != null ? group.getOrganization().getId() : null) // Pega o ID da Organization
                .organizationName(group.getOrganization() != null ? group.getOrganization().getName() : "N/A") // Pega o nome da Organization
                .members(memberDTOs) // Adiciona os DTOs dos membros (se carregados)
                .build();
    }
}