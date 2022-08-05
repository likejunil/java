package june1.vgen.open.domain;

import june1.vgen.open.common.annotation.BackupEntity;
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
