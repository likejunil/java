package june1.open.repository;

import june1.open.domain.Company;
import june1.open.repository.dto.SearchCompanyCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRepositoryCustom {

    Page<Company> findByCond(SearchCompanyCond cond, Pageable pageable);
}
