package june1.vgen.open.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {

    private String accessToken;
    private String refreshToken;
    private Long expired;
}