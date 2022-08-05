package june1.vgen.open.repository;

import june1.vgen.open.domain.RedisToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedisTokenRepository extends JpaRepository<RedisToken, Long> {
}
