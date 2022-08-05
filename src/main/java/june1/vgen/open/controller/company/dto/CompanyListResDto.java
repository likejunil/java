package june1.vgen.open.controller.company.dto;

import june1.vgen.open.domain.enumeration.Corp;
import june1.vgen.open.service.common.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CompanyListResDto {

    private PageInfo pageInfo;
    private List<CompanyDto> list;

    @Getter
    @Builder
    public static class CompanyDto {
        private int no;
        private Long seq;
        private String regiNum;
        private Corp companyType;
        private String companyName;
        private String ceoName;
        private String contactNum;
        private String zipCode;
        private String address;
        private String addressDetail;
    }
}
