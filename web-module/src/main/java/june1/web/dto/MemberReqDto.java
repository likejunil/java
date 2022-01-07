package june1.web.dto;

import june1.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberReqDto {

    private String name;
    private Integer age;

    public Member toMember() {
        return Member.builder().name(this.name).age(this.age).build();
    }
}
