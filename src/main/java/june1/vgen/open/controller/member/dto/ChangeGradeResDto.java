package june1.vgen.open.controller.member.dto;

import june1.vgen.open.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeGradeResDto {

    private Long seq;
    private String memberId;
    private Role role;
}
