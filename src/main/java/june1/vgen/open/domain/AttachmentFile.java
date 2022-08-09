package june1.vgen.open.domain;

import june1.vgen.open.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
