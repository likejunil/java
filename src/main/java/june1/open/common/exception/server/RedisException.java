package june1.open.common.exception.server;

import lombok.Builder;

public class RedisException extends ServerException {

    public RedisException() {
    }

    public RedisException(String message) {
        super(message);
    }

    @Builder
    public RedisException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
