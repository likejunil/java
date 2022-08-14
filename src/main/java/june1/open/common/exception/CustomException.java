package june1.open.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    protected String code;
    protected String message;

    protected String object;
    protected String field;
    protected String rejectedValue;

    public CustomException(String object, String field, String code, String rejectedValue, String message) {
        super(message);
        this.object = object;
        this.field = field;
        this.code = code;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }
}
