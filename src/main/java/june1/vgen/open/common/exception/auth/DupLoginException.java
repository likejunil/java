package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class DupLoginException extends AuthException {

    public DupLoginException() {
    }

    public DupLoginException(String message) {
        super(message);
    }

    @Builder
    public DupLoginException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
