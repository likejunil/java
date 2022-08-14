package june1.open.common.exception.auth;

import lombok.Builder;

//중복로그인 예외
public class DuplicationLoginException extends WrongAuthException {

    public DuplicationLoginException() {
    }

    public DuplicationLoginException(String message) {
        super(message);
    }

    @Builder
    public DuplicationLoginException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
