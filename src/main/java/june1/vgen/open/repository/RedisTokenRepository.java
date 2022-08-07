package june1.vgen.open.repository;

import june1.vgen.open.domain.RedisToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, Long> {
}
