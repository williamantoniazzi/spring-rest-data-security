package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Anotação do Lombok: Gera um construtor com todos os atributos da classe como argumentos.
// **Função:** Facilita a criação de uma nova instância de 'Marathon' fornecendo todos os seus valores de uma vez.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor sem argumentos (vazio).
// **Função:** É essencial para frameworks de persistência como o JPA, que precisam
// instanciar objetos vazios antes de popular seus campos a partir do banco de dados.
@Builder // Anotação do Lombok: Gera o padrão de projeto 'Builder' para a classe.
// **Função:** Permite criar objetos 'Marathon' de forma mais fluente e legível, especialmente se houver muitos campos opcionais.
// Ex: `Marathon.builder().identification("Marathon X").weight(70.5).build();`
@Entity // Anotação JPA: Marca esta classe como uma entidade JPA.
// **Função:** Indica ao JPA que esta classe deve ser mapeada para uma tabela no banco de dados.
@Table(name = "marathons") // Anotação JPA: Especifica o nome da tabela no banco de dados para esta entidade.
// **Função:** Define explicitamente que a tabela correspondente será chamada "marathons".

public class Marathon {

    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "marathonsidgen", sequenceName = "marathons_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marathonsidgen")
    private Long id;
    private String identification;
    private Double weight;
    private Double score;


}