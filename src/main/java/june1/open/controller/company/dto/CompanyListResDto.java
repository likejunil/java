package june1.open.controller.company.dto;

import june1.open.domain.enumeration.Corp;
import june1.open.service.common.PageInfo;
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
        private Corp companyType;
        private String companyName;
    }
}