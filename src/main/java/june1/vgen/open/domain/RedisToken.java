package june1.vgen.open.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@Builder
@RedisHash(value = "token", timeToLive = 600)
public class RedisToken {

    @Id
    //사용자의 고유번호
    private Long id;
    //리프레쉬 토큰
    private String token;
}
