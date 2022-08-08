package june1.vgen.open.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.QCompany;
import june1.vgen.open.domain.QMember;
import june1.vgen.open.repository.dto.SearchCompanyCond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory factory;

    /**
     * 1:n 에서의 fetchJoin() 을 사용..
     *
     * @param cond
     * @param pageable
     * @return
     */
    @Override
    public Page<Company> findByCond(SearchCompanyCond cond, Pageable pageable) {
        //큐클래스 사용
        QMember m = QMember.member;
        QCompany c = QCompany.company;

        //조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        if (cond.getMemberSeq() != null) builder.and(m.id.eq(cond.getMemberSeq()));
        if (cond.getCompanySeq() != null) builder.and(c.id.eq(cond.getCompanySeq()));
        if (cond.getCompanyType() != null) builder.and(c.companyType.eq(cond.getCompanyType()));
        if (cond.getCompanyName() != null) builder.and(c.companyName.eq(cond.getCompanyName()));
        if (cond.getCeoName() != null) builder.and(c.ceoName.eq(cond.getCeoName()));
        if (cond.getRegexCompanyName() != null) builder.and(c.companyName.contains(cond.getRegexCompanyName()));

        //조회
        JPAQuery<Company> query = factory
                .select(c)
                .distinct()
                .from(c)
                .leftJoin(c.members, m)
                .where(builder);

        //총 개수
        int totalCount = query.fetch().size();

        //조회 결과
        pageable.getSort().stream().forEach(o -> {
            PathBuilder<Company> pathBuilder = new PathBuilder<>(c.getType(), c.getMetadata());
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(o.getProperty()));
            query.orderBy(orderSpecifier);
        });
        List<Company> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }
}
