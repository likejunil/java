package june1.vgen.open.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Corp {

    ROLE_CORP_COLOR,
    ROLE_CORP_ANIMAL,
    ROLE_CORP_FRUIT;

    @JsonCreator
    public static Corp from(String s) {
        return Corp.valueOf(s.toUpperCase());
    }
}
