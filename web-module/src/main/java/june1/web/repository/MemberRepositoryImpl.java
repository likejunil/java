package june1.web.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import june1.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static june1.domain.QMember.member;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Member> findAllByAgeGoe(int age) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.age.goe(age));

        return factory
                .select(member)
                .from(member)
                .where(builder)
                .fetch();
    }
}
