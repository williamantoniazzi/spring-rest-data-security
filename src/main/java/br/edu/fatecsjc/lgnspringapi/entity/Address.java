package br.edu.fatecsjc.lgnspringapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Address {

    private String street;
    private String number;
    private String neighborhood;
    private String zipCode;
    private String city;
    private String state;
}
