package june1.vgen.open.repository;

import june1.vgen.open.common.annotation.BackupRepository;
import june1.vgen.open.domain.CompanyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

@BackupRepository
public interface CompanyHistoryRepository extends JpaRepository<CompanyHistory, Long> {

}
