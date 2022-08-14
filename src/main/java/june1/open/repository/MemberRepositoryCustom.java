package june1.open.repository;

import june1.open.domain.Member;
import june1.open.repository.dto.SearchMemberCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<Member> findByCondWithCompany(SearchMemberCond cond, Pageable pageable);
}
