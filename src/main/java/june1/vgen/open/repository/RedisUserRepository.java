package june1.vgen.open.repository;

import june1.vgen.open.domain.RedisUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedisUserRepository extends JpaRepository<RedisUser, Long> {
}
