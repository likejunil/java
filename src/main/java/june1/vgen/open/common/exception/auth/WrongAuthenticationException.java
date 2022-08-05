package june1.vgen.open.common.exception.auth;

//401 인증 예외를 생성할 때, 해당 예외를 상속받아서 처리한다.
public class WrongAuthenticationException extends WrongAuthException {

    public WrongAuthenticationException() {
    }

    public WrongAuthenticationException(String message) {
        super(message);
    }

    public WrongAuthenticationException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
