package br.edu.fatecsjc.lgnspringapi.dto;

import br.edu.fatecsjc.lgnspringapi.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Long groupId;
    private String groupName;
    private List<MarathonResponseDTO> marathons;


    public static MemberResponseDTO fromEntity(Member member) {
        if (member == null) return null;

        List<MarathonResponseDTO> marathons = null;
        if (member.getMarathons() != null) {
            marathons = member.getMarathons().isEmpty()
                    ? Collections.emptyList()
                    : member.getMarathons().stream()
                    .map(MarathonResponseDTO::fromEntity)
                    .toList();
        }

        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .email(member.getEmail())
                .groupId(member.getGroup() != null ? member.getGroup().getId() : null)
                .groupName(member.getGroup() != null ? member.getGroup().getName() : null)
                .marathons(marathons)
                .build();
    }

    @Override
    public String toString() {
        return "MemberResponseDTO(id=" + id +
                ", name=" + name +
                ", age=" + age +
                ", email=" + email +
                ", groupId=" + groupId +
                ", groupName=" + groupName +
                ", marathons=" + marathons + ")";
    }
}