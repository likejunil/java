package june1.vgen.open.common.exception.auth;

import lombok.Builder;

//만료된 토큰 사용 예외
public class ExpiredTokenException extends NotAcceptableException {

    public ExpiredTokenException() {
    }

    public ExpiredTokenException(String message) {
        super(message);
    }

    @Builder
    public ExpiredTokenException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
