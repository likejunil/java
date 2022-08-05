package june1.vgen.open.controller.company.dto;

import june1.vgen.open.domain.enumeration.Corp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class RegisterCompanyReqDto {

    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 ]*[^ ]$")
    private String regiNum;

    private Corp companyType;

    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 ]*[^ ]$")
    private String companyName;

    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 ]*[^ ]$")
    private String ceoName;

    @Email(message = "이메일 형식을 입력해야 합니다.")
    private String email;

    @Size(min = 13, max = 32)
    @Pattern(regexp = "^[^ ][0-9\\-]*[^ ]$")
    private String contactNum;

    @Size(min = 4, max = 12)
    @Pattern(regexp = "^[^ ][0-9]*[^ ]$")
    private String zipCode;

    @Size(min = 1, max = 128)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 \\-]*[^ ]$")
    private String address;

    @Size(min = 1, max = 128)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 \\-]*[^ ]$")
    private String addressDetail;
}