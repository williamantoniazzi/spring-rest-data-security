package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Organization; // Importa a entidade Organization
import br.edu.fatecsjc.lgnspringapi.entity.Group;        // Importa a entidade Group (para a lista de grupos)

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data // Gera getters, setters, toString, equalsAndHashCode
@Builder // Padrão Builder para criação fluida de objetos
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class OrganizationResponseDTO {
    private Long id;
    private String name;
    private AddressDTO address; // Usa o AddressDTO aqui
    private String institutionName;
    private String headquartersCountry;
    private List<GroupResponseDTO> groups; // Para mostrar os grupos associados (usando o DTO de Group)

    /**
     * Converte uma entidade Organization em um DTO de resposta.
     * Gerencia o carregamento lazy da lista de grupos.
     * @param organization A entidade Organization a ser convertida.
     * @return Uma nova instância de OrganizationResponseDTO.
     */
    public static OrganizationResponseDTO fromEntity(Organization organization) {
        if (organization == null) {
            return null; // Retorna nulo se a entidade for nula
        }

        // Mapeia o Address Entity para AddressDTO
        AddressDTO addressDTO = null;
        if (organization.getAddress() != null) {
            addressDTO = AddressDTO.builder()
                    .street(organization.getAddress().getStreet())
                    .number(organization.getAddress().getNumber())
                    .neighborhood(organization.getAddress().getNeighborhood())
                    .zipCode(organization.getAddress().getZipCode())
                    .city(organization.getAddress().getCity())
                    .state(organization.getAddress().getState())
                    .build();
        }

        List<GroupResponseDTO> groupDTOs = null;
        // Verifica se a coleção de grupos foi inicializada para evitar LazyInitializationException
        // O `get(0) != null` é uma verificação extra para garantir que a coleção contém elementos válidos
        // se ela foi carregada de forma lazy, mas é sempre bom garantir a inicialização na camada de serviço.
        if (organization.getGroups() != null && !organization.getGroups().isEmpty()) {
            // Se a coleção estiver inicializada e não vazia, mapeia os grupos.
            groupDTOs = organization.getGroups().stream()
                    .map(GroupResponseDTO::fromEntity) // Chama o mapeamento de Group para cada Group
                    .collect(Collectors.toList());
        }
        // Se a coleção estiver vazia ou não inicializada, 'groupDTOs' permanecerá nulo ou vazio.

        return OrganizationResponseDTO.builder()
                .id(organization.getId())
                .name(organization.getName())
                .address(addressDTO) // Adiciona o DTO do endereço
                .institutionName(organization.getInstitutionName())
                .headquartersCountry(organization.getHeadquartersCountry())
                .groups(groupDTOs) // Adiciona os DTOs dos grupos (se carregados)
                .build();
    }
}