package june1.vgen.open.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@Builder
@RedisHash(value = "refresh_token")
public class RedisRefreshToken implements RefreshTokenInterface {

    @Id
    private Long id;
    private String token;
}
