package june1.open.common.exception.client;

import lombok.Builder;

public class MemberExistException extends ClientException {

    public MemberExistException() {
    }

    public MemberExistException(String message) {
        super(message);
    }

    @Builder
    public MemberExistException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
