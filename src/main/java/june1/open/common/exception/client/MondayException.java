package june1.open.common.exception.client;

import lombok.Builder;

public class MondayException extends ClientException {

    public MondayException() {
    }

    public MondayException(String message) {
        super(message);
    }

    @Builder
    public MondayException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
