package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Marathon; // Importa a entidade Marathon
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class MarathonResponseDTO {
    private Long id;
    private String identification;
    private Double weight;
    private Double score;

    /**
     * Converte uma entidade Marathon em um DTO de resposta.
     * Útil ao enviar dados do banco de volta para a API.
     * @param marathon A entidade Marathon a ser convertida.
     * @return Uma nova instância de MarathonResponseDTO.
     */
    public static MarathonResponseDTO fromEntity(Marathon marathon) {
        if (marathon == null) {
            return null; // Retorna nulo se a entidade for nula
        }
        return MarathonResponseDTO.builder()
                .id(marathon.getId())
                .identification(marathon.getIdentification())
                .weight(marathon.getWeight())
                .score(marathon.getScore())
                .build();
    }
}