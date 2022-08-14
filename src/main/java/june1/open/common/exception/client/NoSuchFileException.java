package june1.open.common.exception.client;

import lombok.Builder;

public class NoSuchFileException extends ClientException {

    public NoSuchFileException() {
    }

    public NoSuchFileException(String message) {
        super(message);
    }

    @Builder
    public NoSuchFileException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
