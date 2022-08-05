package june1.vgen.open.common.exception.auth;

//403 인가 예외를 생성할 때, 해당 예외를 상속받아서 처리한다.
public class WrongAuthoritiesException extends WrongAuthException {

    public WrongAuthoritiesException() {
    }

    public WrongAuthoritiesException(String message) {
        super(message);
    }

    public WrongAuthoritiesException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
