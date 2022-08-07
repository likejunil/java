package june1.vgen.open.repository.dto;

import june1.vgen.open.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMemberCond {

    private Long memberSeq;
    private String memberId;
    private Role role;
    private Long companySeq;
}
