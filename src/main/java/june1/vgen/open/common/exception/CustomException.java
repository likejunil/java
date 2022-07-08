package june1.vgen.open.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomException extends RuntimeException {

    private String object;
    private String field;
    private String code;
    private String rejectedValue;
    private String message;

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }
}
