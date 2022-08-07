package june1.vgen.open.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.QCompany;
import june1.vgen.open.repository.dto.SearchCompanyCond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Company> findByCond(SearchCompanyCond cond) {
        //큐클래스 사용
        QCompany c = QCompany.company;

        //조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        if (cond.getCompanySeq() != null) builder.and(c.id.eq(cond.getCompanySeq()));
        if (cond.getCompanyType() != null) builder.and(c.companyType.eq(cond.getCompanyType()));
        if (cond.getCompanyName() != null) builder.and(c.companyName.eq(cond.getCompanyName()));
        if (cond.getCeoName() != null) builder.and(c.ceoName.eq(cond.getCeoName()));

        //결과 조회
        return factory
                .select(c)
                .distinct()
                .from(c)
                .where(builder)
                .fetch();
    }
}
