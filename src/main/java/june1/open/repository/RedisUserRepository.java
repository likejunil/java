package june1.open.repository;

import june1.open.domain.redis.RedisUser;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserRepository extends CrudRepository<RedisUser, Long> {
}
