package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Address;      // Importa a entidade Address para o mapeamento
import br.edu.fatecsjc.lgnspringapi.entity.Organization; // Importa a entidade Organization

import jakarta.validation.Valid;        // Para validar o objeto AddressDTO aninhado
import jakarta.validation.constraints.NotBlank; // Para validação

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class OrganizationRequestDTO {

    @NotBlank(message = "O nome da organização é obrigatório.")
    private String name;

    @Valid // Garante que as validações dentro de AddressDTO também sejam aplicadas
    private AddressDTO address; // Usa o AddressDTO para compor o endereço

    @NotBlank(message = "O nome da instituição de ensino é obrigatório.")
    private String institutionName;

    @NotBlank(message = "O país sede da organização é obrigatório.")
    private String headquartersCountry;

    /**
     * Converte este DTO de requisição em uma entidade Organization.
     * @return Uma nova instância da entidade Organization.
     */
    public Organization toEntity() {
        Address entityAddress = null;
        if (this.address != null) {
            // Mapeia o AddressDTO para a entidade Address
            entityAddress = Address.builder()
                    .street(this.address.getStreet())
                    .number(this.address.getNumber())
                    .neighborhood(this.address.getNeighborhood())
                    .zipCode(this.address.getZipCode())
                    .city(this.address.getCity())
                    .state(this.address.getState())
                    .build();
        }

        return Organization.builder()
                .name(this.name)
                .address(entityAddress) // Associa a entidade Address
                .institutionName(this.institutionName)
                .headquartersCountry(this.headquartersCountry)
                .build();
    }
}