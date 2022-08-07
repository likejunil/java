package june1.vgen.open.domain;

import june1.vgen.open.domain.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Table;

@Slf4j
@Entity
@Table(name = "be_member")
@Getter
@AllArgsConstructor
@SuperBuilder
public class Member extends MemberCore {

    public Member memberName(String name) {
        this.memberName = name;
        return this;
    }

    public Member email(String email) {
        this.email = email;
        return this;
    }

    public Member phoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        return this;
    }

    public Member role(Role role) {
        this.role = role;
        return this;
    }

    public Member company(Company company) {
        this.company = company;
        return this;
    }

    public Member handOver() {
        return changeGrade(Role.ROLE_MANAGER);
    }

    public Member resign() {
        return changeGrade(Role.ROLE_USER)
                .company(null);
    }

    public Member changeGrade(Role role) {
        this.role = role;
        return this;
    }
}
