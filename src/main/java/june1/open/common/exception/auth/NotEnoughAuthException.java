package june1.open.common.exception.auth;

import lombok.Builder;

//부족한 권한으로 자원 접근..
public class NotEnoughAuthException extends WrongAuthoritiesException {

    public NotEnoughAuthException() {
    }

    public NotEnoughAuthException(String message) {
        super(message);
    }

    @Builder
    public NotEnoughAuthException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
