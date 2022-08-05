package june1.vgen.open.domain;

import june1.vgen.open.domain.enumeration.Corp;
import june1.vgen.open.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@Builder
@RedisHash("user")
public class RedisUser {

    @Id
    private Long id;
    private String userId;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private Long companySeq;
    private Corp companyType;
}
