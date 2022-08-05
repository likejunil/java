package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class WrongAccessException extends AuthException {

    public WrongAccessException() {
    }

    public WrongAccessException(String message) {
        super(message);
    }

    @Builder
    public WrongAccessException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
