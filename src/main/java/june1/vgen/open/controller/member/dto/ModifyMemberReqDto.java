package june1.vgen.open.controller.member.dto;

import june1.vgen.open.domain.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
public class ModifyMemberReqDto {

    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 ]*[^ ]$")
    private String memberName;

    @Email(message = "이메일 형식을 입력해야 합니다.")
    private String email;

    @Size(min = 13, max = 13)
    @Pattern(regexp = "^[^ ][0-9\\-]*[^ ]$")
    private String phoneNum;

    private Role role;

    @Positive
    private Long companySeq;
}
