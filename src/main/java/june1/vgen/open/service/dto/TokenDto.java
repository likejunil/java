package june1.vgen.open.service.dto;

import june1.vgen.open.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

    private Member member;
    private String accessToken;
    private String refreshToken;
}
