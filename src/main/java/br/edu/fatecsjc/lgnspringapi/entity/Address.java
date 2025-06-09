package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter // Anotação do Lombok: Gera automaticamente os métodos 'get' para todos os atributos da classe (ex: getStreet()).
@Setter // Anotação do Lombok: Gera automaticamente os métodos 'set' para todos os atributos da classe (ex: setStreet(String street)).
@ToString // Anotação do Lombok: Gera automaticamente o método 'toString()', útil para depuração e exibição do objeto.
@AllArgsConstructor // Anotação do Lombok: Gera um construtor com todos os atributos da classe como argumentos.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor sem argumentos. Essencial para frameworks como JPA e Spring.
@Builder // Anotação do Lombok: Gera o padrão de projeto 'Builder', que facilita a criação de objetos complexos de forma mais legível.
@Embeddable // Anotação JPA: Marca esta classe como um tipo de valor embutível.
// Isso significa que os atributos de 'Address' serão armazenados como colunas
// na mesma tabela da entidade que 'contém' um objeto 'Address', em vez de em uma tabela separada.

public class Address {

    private String street;
    private String number;
    private String neighborhood;
    private String zipCode;
    private String city;
    private String state;
}
