package june1.open.repository.dto;

import june1.open.domain.enumeration.Role;
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
