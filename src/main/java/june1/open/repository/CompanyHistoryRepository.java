package june1.open.repository;

import june1.open.common.annotation.BackupRepository;
import june1.open.domain.history.CompanyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

@BackupRepository
public interface CompanyHistoryRepository extends JpaRepository<CompanyHistory, Long> {

}
