package june1.vgen.open.controller.common;

import june1.vgen.open.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Error {

    private String objectName;
    private String field;
    private String code;
    private String defaultMessage;
    private Object rejectedValue;

    public static Error noError() {
        return Error.builder()
                .objectName(null)
                .field(null)
                .code(null)
                .defaultMessage(null)
                .rejectedValue(null)
                .build();
    }

    public static List<Error> errors(BindingResult result) {
        return null;
    }

    public static Error by(CustomException e) {
        return Error.builder()
                .code(e.getCode())
                .defaultMessage(e.getMessage())
                .objectName(e.getObject())
                .field(e.getField())
                .rejectedValue(e.getRejectedValue())
                .build();
    }
}
