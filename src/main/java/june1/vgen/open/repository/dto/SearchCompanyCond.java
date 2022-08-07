package june1.vgen.open.repository.dto;

import june1.vgen.open.domain.enumeration.Corp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchCompanyCond {

    private Long companySeq;
    private Corp companyType;
    private String companyName;
    private String ceoName;
}
