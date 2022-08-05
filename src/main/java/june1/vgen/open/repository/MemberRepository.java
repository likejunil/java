package june1.vgen.open.repository;

import june1.vgen.open.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"company"})
    Optional<Member> findByMemberId(String memberId);

    Page<Member> findByCompany_Id(Long seq, Pageable pageable);
}
