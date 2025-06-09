package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group; // Importa a entidade Group
import br.edu.fatecsjc.lgnspringapi.entity.Organization; // Importa a entidade Organization para o mapeamento
import jakarta.validation.constraints.NotBlank; // Para validação
import jakarta.validation.constraints.NotNull; // Para validação
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class GroupRequestDTO {

    @NotBlank(message = "O nome do grupo é obrigatório.") // Garante que o nome não seja vazio
    private String name;

    @NotNull(message = "O ID da organização é obrigatório para associar o grupo.") // Garante que o ID da organização não seja nulo
    private Long organizationId; // Para associar a um Organization existente

    /**
     * Converte este DTO de requisição em uma entidade Group.
     * Este método requer que a entidade Organization correspondente já tenha sido buscada do banco de dados.
     * @param organization A entidade Organization à qual este grupo pertence.
     * @return Uma nova instância da entidade Group.
     */
    public Group toEntity(Organization organization) {
        if (organization == null) {
            throw new IllegalArgumentException("A organização não pode ser nula ao converter GroupRequestDTO para entidade.");
        }
        return Group.builder()
                .name(this.name)
                .organization(organization) // Associa a entidade Organization recebida
                .build();
    }
}
