package june1.vgen.open.repository;

import june1.vgen.open.domain.Company;
import june1.vgen.open.repository.dto.SearchCompanyCond;

import java.util.List;

public interface CompanyRepositoryCustom {

    List<Company> findByCond(SearchCompanyCond cond);
}
