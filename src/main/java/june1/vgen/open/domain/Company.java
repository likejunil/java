package june1.vgen.open.domain;

import june1.vgen.open.domain.enumeration.Corp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "be_company")
@Getter
@AllArgsConstructor
@SuperBuilder
public class Company extends CompanyCore {

    public Company companyType(Corp companyType) {
        this.companyType = companyType;
        return this;
    }

    public Company companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public Company ceoName(String ceoName) {
        this.ceoName = ceoName;
        return this;
    }

    public Company contactNum(String contactNum) {
        this.contactNum = contactNum;
        return this;
    }

    public Company zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public Company address(String address) {
        this.address = address;
        return this;
    }

    public Company addressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
        return this;
    }
}
