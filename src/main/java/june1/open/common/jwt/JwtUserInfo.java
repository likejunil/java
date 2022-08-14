package june1.open.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JwtUserInfo {

    private Long seq;
    private String userId;
    private String email;
    private Long companySeq;
}
