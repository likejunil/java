package june1.vgen.open.repository;

import june1.vgen.open.domain.RedisUser;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserRepository extends CrudRepository<RedisUser, Long> {
}
