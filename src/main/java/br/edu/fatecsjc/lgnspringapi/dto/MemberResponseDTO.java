package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Member;    // Importa a entidade Member
import br.edu.fatecsjc.lgnspringapi.entity.Marathon; // Importa a entidade Marathon (para a lista de maratonas)

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
public class MemberResponseDTO {
    private Long id;
    private String name;
    private Integer age;
    private Long groupId; // ID do grupo (para evitar recursão)
    private String groupName; // Nome do grupo (para visualização rápida)
    private List<MarathonResponseDTO> marathons; // Lista de maratonas participadas (usando DTO)

    /**
     * Converte uma entidade Member em um DTO de resposta.
     * Gerencia o carregamento lazy dos relacionamentos (Group e Marathons).
     * @param member A entidade Member a ser convertida.
     * @return Uma nova instância de MemberResponseDTO.
     */
    public static MemberResponseDTO fromEntity(Member member) {
        if (member == null) {
            return null; // Retorna nulo se a entidade for nula
        }

        Long groupId = null;
        String groupName = null;
        // Verifica e carrega as informações do grupo se a entidade group estiver inicializada
        if (member.getGroup() != null) { // O Group é FetchType.LAZY, então pode ser um proxy não inicializado
            groupId = member.getGroup().getId();
            groupName = member.getGroup().getName(); // Isso vai forçar o carregamento do proxy se não estiver já carregado
        }

        List<MarathonResponseDTO> marathonDTOs = null;
        // Verifica se a coleção de maratonas foi inicializada para evitar LazyInitializationException
        if (member.getMarathons() != null && !member.getMarathons().isEmpty()) {
            // É seguro mapear se a lista não for nula e não vazia.
            // Para FetchType.LAZY, a camada de serviço deve garantir que a lista seja inicializada antes de chamar este método,
            // caso contrário, uma LazyInitializationException pode ocorrer se a sessão JPA estiver fechada.
            marathonDTOs = member.getMarathons().stream()
                    .map(MarathonResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .groupId(groupId)    // ID do grupo
                .groupName(groupName) // Nome do grupo
                .marathons(marathonDTOs) // DTOs das maratonas (se carregadas)
                .build();
    }
}