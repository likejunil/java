package june1.vgen.open.common.exception.auth;

import june1.vgen.open.common.exception.CustomException;

public class AuthException extends CustomException {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
