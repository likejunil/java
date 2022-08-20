package june1.db.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {

    private Long id;
    private String name;
    private long money;
}
