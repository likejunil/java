package june1.web.repository;

import june1.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findAllByAgeGoe(int age);
}
