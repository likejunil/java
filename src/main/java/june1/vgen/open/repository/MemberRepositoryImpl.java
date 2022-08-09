package june1.vgen.open.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.QAttachmentFile;
import june1.vgen.open.domain.QCompany;
import june1.vgen.open.domain.QMember;
import june1.vgen.open.repository.dto.SearchMemberCond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Member> findByCondWithCompany(SearchMemberCond cond, Pageable pageable) {

        //큐클래스 사용
        QMember m = QMember.member;
        QCompany c = QCompany.company;
        QAttachmentFile f = QAttachmentFile.attachmentFile;

        //조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        if (cond.getMemberSeq() != null) builder.and(m.id.eq(cond.getMemberSeq()));
        if (cond.getMemberId() != null) builder.and(m.memberId.eq(cond.getMemberId()));
        if (cond.getRole() != null) builder.and(m.role.eq(cond.getRole()));
        if (cond.getCompanySeq() != null) builder.and(c.id.eq(cond.getCompanySeq()));

        //페치조인으로 회사의 정보를 함께 가져옴..
        JPAQuery<Member> query = factory
                .select(m)
                .from(m)
                .leftJoin(m.company, c).fetchJoin()
                .leftJoin(m.image, f).fetchJoin()
                .where(builder);

        //총 개수
        int totalCount = query.fetch().size();

        //조회 결과
        pageable.getSort().stream().forEach(o -> {
            PathBuilder<Member> pathBuilder = new PathBuilder<>(m.getType(), m.getMetadata());
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(o.getProperty()));
            query.orderBy(orderSpecifier);
        });
        List<Member> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }
}
