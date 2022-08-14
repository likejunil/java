package june1.open.repository.dto;

import june1.open.domain.enumeration.Corp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchCompanyCond {

    private Long memberSeq;
    private Long companySeq;
    private Corp companyType;
    private String companyName;
    private String regexCompanyName;
    private String ceoName;
}
