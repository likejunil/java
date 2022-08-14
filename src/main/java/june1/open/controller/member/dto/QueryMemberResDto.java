package june1.open.controller.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueryMemberResDto {

    private Long seq;
    private String memberId;
    private String memberName;
    private String email;
    private String phoneNum;
    private String role;
    private Long imageSeq;
}
