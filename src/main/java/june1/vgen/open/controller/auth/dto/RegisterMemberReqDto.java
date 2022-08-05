package june1.vgen.open.controller.auth.dto;

import june1.vgen.open.domain.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
public class RegisterMemberReqDto {

    @NotBlank(message = "아이디는 필수 입력값 입니다.")
    @Size(min = 1, max = 32, message = "아이디는 1자 이상 32자 이하여야 합니다.")
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣_-]*[^ ]$",
            message = "아이디는 영문자와 한글, 숫자, 공백문자, 밑줄, 대쉬(-)를 포함할 수 있습니다.")
    private String memberId;

    @NotBlank(message = "암호는 필수 입력값 입니다.")
    @Size(min = 8, max = 12, message = "암호는 8자 이상, 12자 이하여야 합니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,12}",
            message = "암호는 영문자와 숫자, 특수기호를 적어도 1개 이상 포함해야 합니다.")
    private String password;

    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣]*[^ ]$")
    private String memberName;

    @Email(message = "이메일 형식을 입력해야 합니다.")
    private String email;

    @Size(min = 13, max = 13)
    @Pattern(regexp = "^[^ ][0-9\\-]*[^ ]$")
    private String phoneNum;

    @NotNull
    private Role role;

    @Positive
    private Long companySeq;
}
