package june1.vgen.open.repository;

import june1.vgen.open.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {
}
