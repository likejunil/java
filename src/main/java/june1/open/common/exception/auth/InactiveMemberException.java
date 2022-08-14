package june1.open.common.exception.auth;

import lombok.Builder;

//비활성화 계정 로그인 예외
public class InactiveMemberException extends WrongAuthException {

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
