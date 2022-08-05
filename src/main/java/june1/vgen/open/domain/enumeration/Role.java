package june1.vgen.open.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {

    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_USER;

    /**
     * enum 을 요청 dto 에서 사용하려면 다음과 같이 @JsonCreator 애노테이션을 구현해야 한다.
     * 문자열로 올라온 정보를 매칭되는 해당 Role 로 변환해 준다.
     */
    @JsonCreator
    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}
