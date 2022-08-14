package june1.open.domain.redis;

import june1.open.domain.enumeration.Corp;
import june1.open.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@Builder
@RedisHash(value = "user", timeToLive = 300)
public class RedisUser {

    @Id
    //사용자의 고유번호
    private Long id;
    //접근 토큰
    private String accessToken;
    //사용자 권한
    private Role role;

    //아이디
    private String userId;
    //이메일
    private String email;

    //소속 회사의 타입
    private Corp companyType;
    //소속 회사의 고유번호
    private Long companySeq;

    public RedisUser role(Role role) {
        this.role = role;
        return this;
    }

    public RedisUser handOver() {
        return role(Role.ROLE_MANAGER);
    }

    public RedisUser company(Long companySeq, Corp companyType) {
        this.companyType = companyType;
        this.companySeq = companySeq;
        return this;
    }
}
