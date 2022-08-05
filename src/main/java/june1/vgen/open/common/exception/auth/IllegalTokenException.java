package june1.vgen.open.common.exception.auth;

import lombok.Builder;

//잘못된 jwt 토큰 예외
public class IllegalTokenException extends NotAcceptableException {

    public IllegalTokenException() {
    }

    public IllegalTokenException(String message) {
        super(message);
    }

    @Builder
    public IllegalTokenException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
