package june1.db.repository;

import june1.db.domain.Member;

import java.util.List;

public interface MemberRepository {

    Member save(Member m);

    Member findByName(String name);

    List<Member> findAll();

    int update(String name, Long money);

    int delete(Member m);
}
