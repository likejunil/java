package june1.db.controller.dto;

import june1.db.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {

    private Long id;
    private String name;
    private Long money;

    public static MemberDto of(Member m) {
        return MemberDto.builder()
                .id(m.getId())
                .name(m.getName())
                .money(m.getMoney())
                .build();
    }
}
