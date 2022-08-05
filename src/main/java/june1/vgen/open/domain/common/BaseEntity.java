package june1.vgen.open.domain.common;


import june1.vgen.open.common.ConstantInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity extends Backup {

    @Column(name = "create_time", updatable = false)
    protected LocalDateTime createdTime;

    @Column(name = "update_time")
    protected LocalDateTime modifiedTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdTime = now;
        modifiedTime = now;
    }

    @PostPersist
    public void postPersist() {
        backup(ConstantInfo.CREATE);
    }

    @PreUpdate
    public void preUpdate() {
        modifiedTime = LocalDateTime.now();
        backup(ConstantInfo.UPDATE);
    }
}