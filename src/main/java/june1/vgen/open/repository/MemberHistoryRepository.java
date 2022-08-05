package june1.vgen.open.repository;

import june1.vgen.open.common.annotation.BackupRepository;
import june1.vgen.open.domain.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

@BackupRepository
public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {

}
