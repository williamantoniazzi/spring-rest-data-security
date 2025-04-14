package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "organizationsidgen", sequenceName = "organizations_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizationsidgen")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "headquarters_country")
    private String headquartersCountry;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Group> groups;
}
