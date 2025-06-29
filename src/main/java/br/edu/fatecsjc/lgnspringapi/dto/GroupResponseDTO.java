package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String name;
    private Long organizationId;
    private String organizationName;
    private List<MemberResponseDTO> members;

    /**
     * Converte uma entidade Group em um DTO de resposta.
     * @param group A entidade Group a ser convertida.
     * @return Uma nova instância de GroupResponseDTO.
     */
    public static GroupResponseDTO fromEntity(Group group) {
        if (group == null) {
            return null;
        }
        List<MemberResponseDTO> memberDTOs;
        if (group.getMembers() == null) {
            memberDTOs = null;
        } else if (group.getMembers().isEmpty()) {
            memberDTOs = java.util.Collections.emptyList();
        } else {
            memberDTOs = group.getMembers().stream()
                    .map(MemberResponseDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList());
        }

        return GroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .organizationId(group.getOrganization() != null ? group.getOrganization().getId() : null)
                .organizationName(group.getOrganization() != null ? group.getOrganization().getName() : "N/A")
                .members(memberDTOs)
                .build();
    }


    @Override
    public String toString() {
        return "GroupResponseDTO(id=" + id +
                ", name=" + name +
                ", organizationId=" + organizationId +
                ", organizationName=" + organizationName +
                ", members=" + members + ")";
    }
}