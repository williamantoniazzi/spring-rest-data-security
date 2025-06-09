package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter // Anotação do Lombok: Gera automaticamente os métodos 'get' para todos os atributos da classe (ex: getId(), getIdentification()).
// **Função:** Permite que outras partes do código leiam os valores dos atributos desta entidade.
@Setter // Anotação do Lombok: Gera automaticamente os métodos 'set' para todos os atributos da classe (ex: setId(Long id), setIdentification(String identification)).
// **Função:** Permite que outras partes do código modifiquem os valores dos atributos desta entidade.
@ToString // Anotação do Lombok: Gera automaticamente o método 'toString()' para a classe.
// **Função:** Produz uma representação em string do objeto, útil para depuração e logs.
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