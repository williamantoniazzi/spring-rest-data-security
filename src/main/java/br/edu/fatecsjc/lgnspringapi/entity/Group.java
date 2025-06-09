package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Getter // Anotação do Lombok: Gera automaticamente os métodos 'get' (getters) para todos os atributos da classe (ex: getId(), getName()).
// **Função:** Permite que outras partes do seu código leiam os valores dos atributos desta entidade.
@Setter // Anotação do Lombok: Gera automaticamente os métodos 'set' (setters) para todos os atributos da classe (ex: setId(Long id), setName(String name)).
// **Função:** Permite que outras partes do seu código modifiquem os valores dos atributos desta entidade.
@ToString // Anotação do Lombok: Gera automaticamente o método 'toString()' para a classe.
// **Função:** Cria uma representação em string do objeto, o que é muito útil para depuração e para registrar informações.
@AllArgsConstructor // Anotação do Lombok: Gera um construtor com todos os atributos da classe como argumentos.
// **Função:** Facilita a criação de uma nova instância de 'Group' fornecendo todos os seus valores iniciais de uma só vez.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor sem argumentos (vazio).
// **Função:** É um requisito fundamental para frameworks de persistência como o JPA, que precisam
// instanciar objetos vazios antes de popular seus campos com dados do banco de dados.
@Builder // Anotação do Lombok: Gera o padrão de projeto 'Builder' para a classe.
// **Função:** Permite construir objetos 'Group' de forma mais legível e flexível, especialmente útil se a classe tivesse muitos campos opcionais.
// Ex: `Group.builder().name("Grupo Alfa").build();`
@Entity // Anotação JPA: Marca esta classe como uma entidade JPA.
// **Função:** Informa ao JPA que esta classe corresponde a uma tabela no banco de dados.
@Table(name = "groups") // Anotação JPA: Especifica o nome da tabela no banco de dados para esta entidade.
// **Função:** Define explicitamente que a tabela correspondente no banco de dados será chamada "groups".

public class Group {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "groupsidgen", sequenceName = "groups_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupsidgen")
    private Long id;
    private String name;

    @ManyToOne // UM grupo pertence a UMA organização
    @JoinColumn(name = "organization_id", nullable = false) // Coluna de chave estrangeira na tabela 'groups'
    private Organization organization; // Atributo que faz referência à Organization

    @OneToMany(mappedBy="group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // Anotação JPA: Define um relacionamento um-para-muitos (um grupo pode ter muitos membros).
    // `mappedBy="group"`: Indica que o mapeamento é feito pelo atributo 'group' na classe 'Member'.
    //                    Isso significa que a coluna de chave estrangeira para 'Group' estará na tabela 'members'.
    // `fetch = FetchType.EAGER`: Indica que os membros associados a este grupo devem ser carregados
    //                            imediatamente do banco de dados junto com o grupo.
    //                            **Atenção:** 'EAGER' pode causar problemas de performance com grandes volumes de dados.
    // `cascade = CascadeType.ALL`: Operações (persist, merge, remove, refresh, detach) realizadas em 'Group'
    //                              serão propagadas para as entidades 'Member' associadas.
    //                              Por exemplo, se um grupo for deletado, seus membros também serão.
    // **Função:** Gerencia o relacionamento entre um grupo e a lista de membros que pertencem a ele.
    // @LazyCollection(LazyCollectionOption.FALSE) // Esta anotação do Hibernate é redundante com FetchType.EAGER
    //                                         // e poderia ser usada para controlar o carregamento "lazy" (preguiçoso).
    //                                         // Com FetchType.EAGER, a coleção já é carregada imediatamente.
    private List<Member> members;
}
