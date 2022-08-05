package june1.vgen.open.repository;

import june1.vgen.open.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember_Id(Long memberSeq);

    void deleteByMember_Id(Long memberSeq);
}
