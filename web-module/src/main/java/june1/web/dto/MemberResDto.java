package june1.web.dto;

import june1.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberResDto {

    private Long id;
    private String name;
    private Integer age;

    public static MemberResDto fromMember(Member m) {
        return MemberResDto.builder()
                .id(m.getId())
                .name(m.getName())
                .age(m.getAge())
                .build();
    }
}
