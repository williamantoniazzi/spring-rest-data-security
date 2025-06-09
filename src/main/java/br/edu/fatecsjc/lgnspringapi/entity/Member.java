package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter // Anotação do Lombok: Gera automaticamente os métodos 'get' para todos os atributos da classe (ex: getName()).
// Função: Permite acessar os valores dos atributos da entidade de forma padronizada.
@Setter // Anotação do Lombok: Gera automaticamente os métodos 'set' para todos os atributos da classe (ex: setName(String name)).
// Função: Permite modificar os valores dos atributos da entidade.
@ToString // Anotação do Lombok: Gera automaticamente o método 'toString()' para a classe.
// Função: Útil para depuração, pois permite imprimir facilmente o estado de um objeto 'Member'.
@AllArgsConstructor // Anotação do Lombok: Gera um construtor com todos os atributos da classe como argumentos.
// Função: Permite criar uma instância de 'Member' inicializando todos os seus campos.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor sem argumentos.
// Função: Essencial para o JPA e outros frameworks que precisam instanciar a entidade antes de preencher seus dados.
@Builder // Anotação do Lombok: Gera o padrão de projeto 'Builder' para a classe.
// Função: Torna a construção de objetos mais legível e flexível, especialmente para entidades com muitos campos.
@Entity // Anotação JPA: Marca esta classe como uma entidade JPA.
// Função: Indica que esta classe será mapeada para uma tabela no banco de dados.
@Table(name = "members") // Anotação JPA: Especifica o nome da tabela no banco de dados para esta entidade.
// Função: Define explicitamente que a tabela correspondente será chamada "members".

public class Member {

    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "membersidgen", sequenceName = "members_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "membersidgen")
    private Long id;

    private String name;
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY) // Anotação JPA: Define um relacionamento muitos-para-um com a entidade 'Group'.
    // Função: Muitos membros podem pertencer a um único grupo.
    @JoinColumn(name = "group_id", nullable = false)
    // Anotação JPA: Especifica a coluna de chave estrangeira na tabela 'members' que se refere à chave primária da tabela 'groups'.
    // name = "group_id": O nome da coluna de chave estrangeira será 'group_id'.
    // nullable = false: Indica que esta coluna não pode ser nula, ou seja, um membro deve pertencer a um grupo.
    // Função: Estabelece o link no banco de dados entre 'Member' e 'Group'.
    private Group group;

    @ManyToMany // Anotação JPA: Define um relacionamento muitos-para-muitos com a entidade 'Marathon'.
    // Função: Um membro pode participar de várias maratonas, e uma maratona pode ter vários membros.
    @JoinTable(
            name = "member_marathons", // Define o nome da tabela de junção (intermediária) no banco de dados.
            joinColumns = @JoinColumn(name = "member_id"), // Coluna na tabela de junção que se refere ao 'Member'.
            inverseJoinColumns = @JoinColumn(name = "marathon_id") // Coluna na tabela de junção que se refere à 'Marathon'.
    )
    // Função: Cria uma tabela intermediária ('member_marathons') para gerenciar o relacionamento N:N,
    // que armazena pares de IDs de membros e maratonas.
    @ToString.Exclude // Anotação do Lombok: Exclui este campo do método 'toString()' gerado.
    // Função: Previne problemas de recursão infinita se 'Marathon' também tiver um 'toString()' que inclui 'Member'.
    private List<Marathon> marathons;
}
