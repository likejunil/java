package june1.open.controller.common;

import june1.open.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

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

    public static Error by(CustomException e) {
        return Error.builder()
                .code(e.getCode())
                .defaultMessage(e.getMessage())
                .objectName(e.getObject())
                .field(e.getField())
                .rejectedValue(e.getRejectedValue())
                .build();
    }

    public static List<Error> errors(BindingResult result, MessageSource messageSource) {
        List<Error> errors = result.getFieldErrors()
                .stream()
                .map(m -> Error.builder()
                        .objectName(m.getObjectName())
                        .field(m.getField())
                        .code(m.getCode())
                        .rejectedValue(m.getRejectedValue())
                        .defaultMessage(messageSource.getMessage(m, Locale.KOREA))
                        .build())
                .collect(toList());

        errors.addAll(result.getAllErrors()
                .stream()
                .filter(DefaultMessageSourceResolvable::shouldRenderDefaultMessage)
                .map(m -> Error.builder()
                        .objectName(m.getObjectName())
                        .code(m.getCode())
                        .defaultMessage(messageSource.getMessage(m, Locale.KOREA))
                        .build())
                .collect(toList()));

        return errors;
    }
}
