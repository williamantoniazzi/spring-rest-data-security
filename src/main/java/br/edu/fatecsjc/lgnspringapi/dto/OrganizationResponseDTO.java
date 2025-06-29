package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponseDTO {
    private Long id;
    private String name;
    private AddressDTO address;
    private String institutionName;
    private String headquartersCountry;
    private List<GroupResponseDTO> groups;

    public static OrganizationResponseDTO fromEntity(Organization organization) {
        if (organization == null) return null;

        List<GroupResponseDTO> groupDTOs = null;
        if (organization.getGroups() != null) {
            groupDTOs = organization.getGroups().isEmpty()
                    ? Collections.emptyList()
                    : organization.getGroups().stream()
                        .map(GroupResponseDTO::fromEntity)
                        .collect(Collectors.toList());
        }

        return OrganizationResponseDTO.builder()
                .id(organization.getId())
                .name(organization.getName())
                .address(organization.getAddress() != null ? AddressDTO.fromEntity(organization.getAddress()) : null)
                .institutionName(organization.getInstitutionName())
                .headquartersCountry(organization.getHeadquartersCountry())
                .groups(groupDTOs)
                .build();
    }
}