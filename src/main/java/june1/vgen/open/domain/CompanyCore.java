package june1.vgen.open.domain;

import june1.vgen.open.domain.common.BaseEntity;
import june1.vgen.open.domain.enumeration.Corp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

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

    protected String companyName;
    protected String regiNum;
    protected String ceoName;
    protected String zipCode;
    protected String address;
    protected String addressDetail;
    protected String contactNum;

    @Enumerated(EnumType.STRING)
    protected Corp companyType;
}
