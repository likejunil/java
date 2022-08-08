package june1.vgen.open.controller.auth.dto;

import june1.vgen.open.domain.enumeration.Corp;
import june1.vgen.open.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResDto {

    private Long seq;
    private String accessToken;
    private String refreshToken;
    private Role role;
    private Corp companyType;
}
