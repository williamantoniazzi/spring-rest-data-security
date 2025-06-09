package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Member;    // Importa a entidade Member
import br.edu.fatecsjc.lgnspringapi.entity.Group;    // Importa a entidade Group para mapeamento
import br.edu.fatecsjc.lgnspringapi.entity.Marathon; // Importa a entidade Marathon para mapeamento

import jakarta.validation.constraints.Min;        // Para validação
import jakarta.validation.constraints.NotBlank;   // Para validação
import jakarta.validation.constraints.NotNull;    // Para validação

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors; // Necessário se for converter uma lista de IDs para entidades

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class MemberRequestDTO {

    @NotBlank(message = "O nome do membro é obrigatório.") // Garante que o nome não seja vazio
    private String name;

    @NotNull(message = "A idade do membro é obrigatória.") // Garante que a idade não seja nula
    @Min(value = 0, message = "A idade não pode ser negativa.") // Garante que a idade não seja negativa
    private Integer age;

    @NotNull(message = "O ID do grupo é obrigatório para associar o membro.") // Garante que o ID do grupo não seja nulo
    private Long groupId; // ID do grupo ao qual o membro pertence

    // Lista de IDs das maratonas participadas.
    // Opcional, pois um membro pode ser criado sem ter participado de maratonas ainda.
    private List<Long> marathonIds;

    /**
     * Converte este DTO de requisição em uma entidade Member.
     * Este método requer que as entidades Group e Marathon correspondentes já tenham sido buscadas do banco de dados.
     * @param group A entidade Group à qual este membro pertence.
     * @param marathons Uma lista de entidades Marathon das quais este membro participou. Pode ser nula ou vazia.
     * @return Uma nova instância da entidade Member.
     */
    public Member toEntity(Group group, List<Marathon> marathons) {
        if (group == null) {
            throw new IllegalArgumentException("O grupo não pode ser nulo ao converter MemberRequestDTO para entidade.");
        }
        return Member.builder()
                .name(this.name)
                .age(this.age)
                .group(group)         // Associa a entidade Group recebida
                .marathons(marathons) // Associa a lista de entidades Marathon (pode ser nula)
                .build();
    }
}