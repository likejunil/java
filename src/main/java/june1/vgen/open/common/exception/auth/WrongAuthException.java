package june1.vgen.open.common.exception.auth;

import june1.vgen.open.common.exception.CustomException;

//모든 인증과 인가 관련 예외는 해당 예외를 상속받아서 처리한다.
public class WrongAuthException extends CustomException {

    public WrongAuthException() {
    }

    public WrongAuthException(String message) {
        super(message);
    }

    public WrongAuthException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
