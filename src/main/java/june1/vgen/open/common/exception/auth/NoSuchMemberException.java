package june1.vgen.open.common.exception.auth;

import lombok.Builder;

public class NoSuchMemberException extends AuthException {

    public NoSuchMemberException() {
    }

    public NoSuchMemberException(String message) {
        super(message);
    }

    @Builder
    public NoSuchMemberException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
