package june1.open.domain;

import june1.open.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static june1.open.common.ConstantInfo.FILE_ROOT_PATH;

@Entity
@Table(name = "be_attachment_file")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long id;

    private String path;
    private String name;
    private String ext;
    private String stored;
    private long size;
    private long useCount;
    private Boolean inUse;

    public AttachmentFile delete() {
        this.inUse = false;
        return this;
    }

    public String getStoredFileFullName() {
        return FILE_ROOT_PATH + path + "/" + stored;
    }
}
