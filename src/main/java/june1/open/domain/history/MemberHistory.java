package june1.open.domain.history;

import june1.open.common.annotation.BackupEntity;
import june1.open.domain.MemberCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "be_member_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@BackupEntity
public class MemberHistory extends MemberCore {

    @Embedded
    private History history;
}
