package june1.vgen.open.controller.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ModifyMemberReqDto {

    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 ]*[^ ]$")
    private String memberName;

    @Email(message = "이메일 형식을 입력해야 합니다.")
    private String email;

    @Size(min = 13, max = 13)
    @Pattern(regexp = "^[^ ][0-9\\-]*[^ ]$")
    private String phoneNum;
}
