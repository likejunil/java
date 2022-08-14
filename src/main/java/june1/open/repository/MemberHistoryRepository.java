package june1.open.repository;

import june1.open.common.annotation.BackupRepository;
import june1.open.domain.history.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

@BackupRepository
public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {

}
