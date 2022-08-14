package june1.open.common.exception.auth;

import lombok.Builder;

//잘못된 refresh token 사용
public class IllegalRefreshTokenException extends WrongAuthenticationException {

    public IllegalRefreshTokenException() {
    }

    public IllegalRefreshTokenException(String message) {
        super(message);
    }

    @Builder
    public IllegalRefreshTokenException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
