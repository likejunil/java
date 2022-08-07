package june1.vgen.open.controller.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class LoginReqDto {

    @NotBlank(message = "아이디는 필수 입력값 입니다.")
    @Size(min = 1, max = 32, message = "아이디는 1자 이상 32자 이하여야 합니다.")
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣_-]*[^ ]$",
            message = "아이디는 영문자와 한글, 숫자, 공백문자, 밑줄, 대쉬(-)를 포함할 수 있습니다.")
    private String userId;

    @NotBlank(message = "암호는 필수 입력값 입니다.")
    @Size(min = 8, max = 12, message = "암호는 8자 이상, 12자 이하여야 합니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,12}",
            message = "암호는 영문자와 숫자, 특수기호를 적어도 1개 이상 포함해야 합니다.")
    private String password;
}
