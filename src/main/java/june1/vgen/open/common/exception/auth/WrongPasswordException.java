package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class WrongPasswordException extends AuthException {

    public WrongPasswordException() {
    }

    public WrongPasswordException(String message) {
        super(message);
    }

    @Builder
    public WrongPasswordException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
