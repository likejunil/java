package june1.vgen.open.common.exception.server;

import lombok.Builder;

public class TokenException extends ServerException {

    public TokenException() {
    }

    public TokenException(String message) {
        super(message);
    }

    @Builder
    public TokenException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
