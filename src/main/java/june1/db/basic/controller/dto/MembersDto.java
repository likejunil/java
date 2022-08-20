package june1.db.basic.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MembersDto {

    private List<MemberDto> list;
}
