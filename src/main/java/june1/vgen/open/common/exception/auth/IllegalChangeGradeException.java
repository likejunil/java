package june1.vgen.open.common.exception.auth;

import lombok.Builder;

//잘못된 권한 변경 예외
public class IllegalChangeGradeException extends WrongAuthoritiesException {

    public IllegalChangeGradeException() {
    }

    public IllegalChangeGradeException(String message) {
        super(message);
    }

    @Builder
    public IllegalChangeGradeException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
