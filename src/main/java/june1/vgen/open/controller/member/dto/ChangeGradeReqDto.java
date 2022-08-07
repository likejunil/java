package june1.vgen.open.controller.member.dto;

import june1.vgen.open.domain.enumeration.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class ChangeGradeReqDto {

    @NotNull
    @Positive
    private Long seq;

    @NotNull
    private Role role;
}
