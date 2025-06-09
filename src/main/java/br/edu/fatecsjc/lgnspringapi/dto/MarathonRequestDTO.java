package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Marathon; // Importa a entidade Marathon
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
public class MarathonRequestDTO {

    @NotBlank(message = "A identificação da maratona é obrigatória.") // Garante que a identificação não seja vazia
    private String identification;

    @NotNull(message = "O peso da maratona é obrigatório.") // Garante que o peso não seja nulo
    private Double weight;

    @NotNull(message = "O score da maratona é obrigatório.") // Garante que o score não seja nulo
    private Double score;

    /**
     * Converte este DTO de requisição em uma entidade Marathon.
     * Útil ao receber dados da API para salvar no banco.
     * @return Uma nova instância da entidade Marathon.
     */
    public Marathon toEntity() {
        return Marathon.builder()
                .identification(this.identification)
                .weight(this.weight)
                .score(this.score)
                .build();
    }
}