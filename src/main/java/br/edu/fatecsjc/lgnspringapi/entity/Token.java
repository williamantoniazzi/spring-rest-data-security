package br.edu.fatecsjc.lgnspringapi.entity;

import br.edu.fatecsjc.lgnspringapi.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation: Provides getters, setters, toString(), equals(), and hashCode() methods automatically.
// Its function is to make the class less verbose and easier to manage.
@Builder // Lombok annotation: Allows you to create instances of 'Token' using a builder pattern.
// Its function is to provide a more readable and flexible way to construct objects, especially with many fields.
@NoArgsConstructor // Lombok annotation: Generates a public no-argument constructor.
// Its function is to satisfy the JPA specification, which requires entities to have a no-arg constructor.
@AllArgsConstructor // Lombok annotation: Generates a public constructor with all fields.
// Its function is to provide a convenient way to initialize all fields when creating a new 'Token' object.
@Entity // JPA annotation: Marks this class as a JPA entity.
// Its function is to tell the JPA provider (like Hibernate) that this class should be mapped to a database table.
@Table(name = "tokens") // JPA annotation: Specifies the name of the database table for this entity.
// Its function is to explicitly name the table "tokens", instead of relying on the default class name.

public class Token {

    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "tokensidgen", sequenceName = "tokens_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokensidgen")
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY) // JPA annotation: Defines a Many-to-One relationship with the 'User' entity.
    // fetch = FetchType.LAZY: The associated 'User' object is loaded only when it's explicitly accessed.
    // Its function is to optimize performance by not loading the 'User' data unless necessary.
    @JoinColumn(name = "user_id") // JPA annotation: Specifies the foreign key column in the 'tokens' table that links to the 'users' table.
    // name = "user_id": The foreign key column will be named 'user_id'.
    // Its function is to establish the link between a token and the user it belongs to.
    private User user;
}
