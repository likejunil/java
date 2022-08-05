package june1.vgen.open.controller.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Response<T> {

    private final static String RES_OK = "OK";
    private final static String RES_ERROR = "ERROR";

    private LocalDateTime transactionTime;
    private String resultCode;
    private List<Error> errors;
    private T data;

    public static <T> Response<T> ok(T data) {
        return Response.<T>builder()
                .transactionTime(LocalDateTime.now())
                .resultCode(RES_OK)
                .errors(List.of(Error.noError()))
                .data(data)
                .build();
    }

    public static <T> Response<T> error(T data, BindingResult result, MessageSource messageSource) {
        return Response.<T>builder()
                .transactionTime(LocalDateTime.now())
                .resultCode(RES_ERROR)
                .errors(Error.errors(result, messageSource))
                .data(data)
                .build();
    }

    public static <T> Response<T> error(T data, Error error) {
        return Response.<T>builder()
                .transactionTime(LocalDateTime.now())
                .resultCode(RES_ERROR)
                .errors(List.of(error))
                .data(data)
                .build();
    }
}
