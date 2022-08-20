package june1.db.basic.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {

    private Long id;
    private String name;
    private long money;
}
