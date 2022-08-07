package june1.vgen.open.domain;

import june1.vgen.open.domain.common.BaseEntity;
import june1.vgen.open.domain.enumeration.Role;
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
public abstract class MemberCore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    protected Long id;

    protected Boolean inUse;
    protected String memberId;
    protected String password;
    protected String memberName;
    protected String email;
    protected String phoneNum;

    @Enumerated(EnumType.STRING)
    protected Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_seq")
    protected Company company;
}
