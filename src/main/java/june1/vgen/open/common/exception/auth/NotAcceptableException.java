package june1.vgen.open.common.exception.auth;

//406 응답, 리프레쉬 토큰을 통해 토큰을 재발급 하거나 로그인을 새롭게 해야 하는 예외
public class NotAcceptableException extends WrongAuthException {

    public NotAcceptableException() {
    }

    public NotAcceptableException(String message) {
        super(message);
    }

    public NotAcceptableException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
