package june1.vgen.open.domain;

import june1.vgen.open.domain.common.BaseEntity;
import june1.vgen.open.domain.enumeration.Corp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class CompanyCore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    protected Long id;

    @Enumerated(EnumType.STRING)
    protected Corp companyType;

    protected String regiNum;
    protected String companyName;
    protected String ceoName;
    protected String email;
    protected String contactNum;
    protected String zipCode;
    protected String address;
    protected String addressDetail;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    protected List<Member> members;
}
