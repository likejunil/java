package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class InactiveMemberException extends AuthException {

    public InactiveMemberException() {
    }

    public InactiveMemberException(String message) {
        super(message);
    }

    @Builder
    public InactiveMemberException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
