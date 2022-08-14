package june1.open.repository;

import june1.open.domain.redis.RedisToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, Long> {
}
