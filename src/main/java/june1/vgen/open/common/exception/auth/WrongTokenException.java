package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class WrongTokenException extends AuthException {

    public WrongTokenException() {
    }

    public WrongTokenException(String message) {
        super(message);
    }

    @Builder
    public WrongTokenException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
