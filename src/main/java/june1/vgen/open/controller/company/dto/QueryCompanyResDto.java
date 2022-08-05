package june1.vgen.open.controller.company.dto;

import june1.vgen.open.domain.enumeration.Corp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueryCompanyResDto {

    private String regiNum;
    private Corp companyType;
    private String companyName;
    private String ceoName;
    private String contactNum;
    private String zipCode;
    private String address;
    private String addressDetail;
}
