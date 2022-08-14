package june1.open.controller.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class FriendsDto {

    private List<Friend> list;

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class Friend {
        private String name;
        private int age;
    }
}
