package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter // Anotação do Lombok: Gera automaticamente os métodos 'get' para todos os atributos da classe (ex: getName()).
// Função: Permite acessar os valores dos atributos da entidade de forma padronizada.
@Setter // Anotação do Lombok: Gera automaticamente os métodos 'set' para todos os atributos da classe (ex: setName(String name)).
// Função: Permite modificar os valores dos atributos da entidade.
@ToString // Anotação do Lombok: Gera automaticamente o método 'toString()' para a classe.
// Função: Útil para depuração, pois permite imprimir facilmente o estado de um objeto 'Organization'.
@AllArgsConstructor // Anotação do Lombok: Gera um construtor com todos os atributos da classe como argumentos.
// Função: Permite criar uma instância de 'Organization' inicializando todos os seus campos.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor sem argumentos.
// Função: Essencial para o JPA e outros frameworks que precisam instanciar a entidade antes de preencher seus dados.
@Builder // Anotação do Lombok: Gera o padrão de projeto 'Builder' para a classe.
// Função: Torna a construção de objetos mais legível e flexível, especialmente para entidades com muitos campos.
@Entity // Anotação JPA: Marca esta classe como uma entidade JPA.
// Função: Indica que esta classe será mapeada para uma tabela no banco de dados.
@Table(name = "organizations") // Anotação JPA: Especifica o nome da tabela no banco de dados para esta entidade.
// Função: Define explicitamente que a tabela correspondente será chamada "organizations".

public class Organization {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "organizationsidgen", sequenceName = "organizations_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizationsidgen")
    private Long id;

    private String name;

    private String cnpj;

    @Embedded
    private Address address;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "headquarters_country")
    private String headquartersCountry;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    // Anotação JPA: Define um relacionamento um-para-muitos (uma organização tem muitos grupos).
    // mappedBy = "organization": Indica que o mapeamento é feito pelo atributo 'organization' na classe 'Group'.
    //                           (Ou seja, a coluna de chave estrangeira está na tabela 'groups').
    // cascade = CascadeType.ALL: Operações (persist, merge, remove, refresh, detach) em 'Organization'
    //                            serão propagadas para as entidades 'Group' associadas.
    // orphanRemoval = true: Se um 'Group' for removido da lista 'groups' de uma 'Organization',
    //                       ele será automaticamente removido do banco de dados (considerado "órfão").
    // Função: Gerencia o relacionamento com os grupos pertencentes a esta organização.
    @ToString.Exclude // Anotação do Lombok: Exclui este campo do método 'toString()' gerado.
    // Função: Previne problemas de recursão infinita se 'Group' também tiver um 'toString()' que inclui 'Organization'.
    private List<Group> groups; // Default fetch type for @OneToMany is LAZY
}
