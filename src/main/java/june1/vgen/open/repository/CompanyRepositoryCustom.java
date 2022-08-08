package june1.vgen.open.repository;

import june1.vgen.open.domain.Company;
import june1.vgen.open.repository.dto.SearchCompanyCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRepositoryCustom {

    Page<Company> findByCond(SearchCompanyCond cond, Pageable pageable);
}
