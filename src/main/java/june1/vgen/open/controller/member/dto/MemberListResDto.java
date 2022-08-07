package june1.vgen.open.controller.member.dto;

import june1.vgen.open.service.common.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberListResDto {

    private PageInfo pageInfo;
    private List<MemberDto> list;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MemberDto {
        private int no;
        private Long seq;
        private Boolean inUse;
        private String memberId;
        private String role;
    }
}
