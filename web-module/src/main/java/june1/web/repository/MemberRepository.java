package june1.web.repository;

import june1.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends
        JpaRepository<Member, Long>, MemberRepositoryCustom {

}
