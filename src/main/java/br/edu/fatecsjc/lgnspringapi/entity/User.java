package br.edu.fatecsjc.lgnspringapi.entity;

import br.edu.fatecsjc.lgnspringapi.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data // Anotação do Lombok: Combina @Getter, @Setter, @ToString, @EqualsAndHashCode e @RequiredArgsConstructor.
// Função: Fornece automaticamente métodos boilerplate para acesso e manipulação de atributos, além de métodos de utilidade.
@Builder // Anotação do Lombok: Habilita o uso do padrão Builder para criar instâncias de User.
// Função: Permite construir objetos User de forma fluente e legível, útil para objetos com muitos atributos opcionais.
@NoArgsConstructor // Anotação do Lombok: Gera um construtor público sem argumentos.
// Função: É obrigatório para JPA e muitos frameworks de persistência para instanciar a entidade antes de preencher seus dados.
@AllArgsConstructor // Anotação do Lombok: Gera um construtor público com todos os atributos da classe como argumentos.
// Função: Permite criar uma instância completa de User em uma única chamada.
@Entity // Anotação JPA: Marca esta classe como uma entidade JPA.
// Função: Indica que esta classe será mapeada para uma tabela no banco de dados.
@Table(name = "users") // Anotação JPA: Especifica o nome da tabela no banco de dados para esta entidade.
// Função: Define explicitamente que a tabela correspondente será chamada "users" (boa prática para evitar nomes padrão).

public class User implements UserDetails {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "usersidgen", sequenceName = "users_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersidgen")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    // Anotação JPA: Define um relacionamento um-para-muitos com a entidade 'Token'.
    // mappedBy = "user": Indica que o mapeamento é feito pelo atributo 'user' na classe 'Token'.
    // (Ou seja, a coluna de chave estrangeira está na tabela 'tokens').
    // Função: Um usuário pode ter múltiplos tokens associados a ele (por exemplo, tokens de sessão, tokens de redefinição de senha).
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
