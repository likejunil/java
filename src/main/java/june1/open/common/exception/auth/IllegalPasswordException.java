package june1.open.common.exception.auth;

import lombok.Builder;

//잘못된 password 예외
public class IllegalPasswordException extends WrongAuthenticationException {

    public IllegalPasswordException() {
    }

    public IllegalPasswordException(String message) {
        super(message);
    }

    @Builder
    public IllegalPasswordException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
